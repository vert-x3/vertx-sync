package io.vertx.ext.sync.npe.verticle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.mongodb.MongoException;

import io.vertx.ext.sync.npe.model.User;
import io.vertx.ext.sync.npe.service.user.UserDao;
import io.vertx.ext.sync.npe.service.user.UserDaoImpl;
import io.vertx.ext.sync.npe.verticle.AbstractSyncVerticle;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.VertxException;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.sync.Sync;
import io.vertx.serviceproxy.ServiceBinder;

public class DaoSyncVerticle extends AbstractSyncVerticle 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DaoSyncVerticle.class);
	
	private final Collection<MessageConsumer<JsonObject>> serviceConsumers = new ArrayList<>(); 

	private ServiceBinder serviceBinder;
	
	private MongoClient mongoClient;
	
	private UserDaoImpl userDao;
	
	@Suspendable
	@Override
	protected void onStarting(final Future<Void> onStartingFuture) 
	{
		try
		{
			LOGGER.info("Initializing DaoSyncVerticle ...");
			
			serviceBinder = new ServiceBinder(vertx);
			
			final JsonObject config = createMongoConfiguration();
			registerUserDao(config);
			
			mongoClient = MongoClient.createNonShared(vertx, config);

			new Fiber<Void>(() -> {
				Void res = Sync.awaitResult(h -> createMongoCollectionsAndCreateIndexesIfNeeded(h));
				res = Sync.awaitResult(h -> initUserDataIfNeeded(h));
				res = Sync.awaitResult(h -> afterMongoStarting(h));
			}).start().get();
			
			LOGGER.info("DaoSyncVerticle starts with success.   [ OK ]");
			onStartingFuture.complete();
		}
		catch (MongoException | VertxException | ExecutionException | InterruptedException me)
		{
			LOGGER.info("Fail to start DaoSyncVerticle: ", me);
			onStartingFuture.fail(me);
		}
	}
	
	@Suspendable
	@Override
	protected void onStoping(final Future<Void> onStopingFuture) 
	{
		if (serviceBinder != null)
		{
			for(MessageConsumer<JsonObject> serviceConsumer : serviceConsumers)
			{
				serviceBinder.unregister(serviceConsumer);
			}
		}
		
		if (mongoClient != null)
		{
			mongoClient.close();
		}
		
		onStopingFuture.complete();
	}
	
	protected JsonObject createMongoConfiguration() 
	{
		String user = System.getProperty("userdb", null);
		String mdp = System.getProperty("mdpdb", null);
		String server = System.getProperty("serverdb", "192.168.0.2");
		String port = System.getProperty("portdb", "27017");
		String name = System.getProperty("namedb", "vertx-sync");
		
		
		if (user != null && mdp != null)
		{
			LOGGER.info("Connecting to mongodb://" + user + ":******@" + server + ":" + port + "/" + name);
		}
		else
		{
			LOGGER.info("Connecting to mongodb://" + server + ":" + port + "/" + name);
		}
		
		return new JsonObject()
	        .put("connection_string", String.format("mongodb://%s%s:%s/%s", ((user != null && mdp != null) ? user + ":" + mdp + "@" : ""), server, port.toString(), name))
	        .put("db_name", name);
	}

	protected void createMongoCollectionsAndCreateIndexesIfNeeded(final Handler<AsyncResult<Void>> result) 
	{
		Void res;

		try
		{
			final List<String> collections = Sync.awaitResult(h -> mongoClient.getCollections(h));
			if (!collections.contains(UserDaoImpl.USER_COLLECTION_NAME))
			{
				res = Sync.awaitResult(h -> mongoClient.createCollection(UserDaoImpl.USER_COLLECTION_NAME, h));
				
				final JsonObject jsonObjectForCreateIndexOnEmail = new JsonObject().put("email", 1).put("unique", true);
				res = Sync.awaitResult(h -> mongoClient.createIndex(UserDaoImpl.USER_COLLECTION_NAME, jsonObjectForCreateIndexOnEmail, h));
			}
			
			result.handle(Future.succeededFuture());
		}
		catch (Exception e)
		{
			result.handle(Future.failedFuture(e));
		}
	}

	protected void initUserDataIfNeeded(final Handler<AsyncResult<Void>> result) 
	{
		final User julienPongeUser = new User();
		julienPongeUser.setFamilyName("PONGE");
		julienPongeUser.setGivenName("Julien");
		julienPongeUser.setEmail("julien@ponge.org");
		
		final User julienVietUser = new User();
		julienVietUser.setFamilyName("VIET");
		julienVietUser.setGivenName("Julien");
		julienVietUser.setEmail("julien@julienviet.com");
		
		try
		{
			String userId = Sync.awaitResult(h -> mongoClient.save(UserDaoImpl.USER_COLLECTION_NAME, serializeToJson(julienPongeUser), h));
			julienPongeUser.setId(userId);
			
			userId = Sync.awaitResult(h -> mongoClient.save(UserDaoImpl.USER_COLLECTION_NAME, serializeToJson(julienVietUser), h));
			julienVietUser.setId(userId);
			
			result.handle(Future.succeededFuture());
		}
		catch (Exception e)
		{
			result.handle(Future.failedFuture(e));
		}
	}
	
	protected void afterMongoStarting(final Handler<AsyncResult<Void>> result) 
	{
		result.handle(Future.succeededFuture());
	}
	
	private void registerUserDao(final JsonObject config) 
	{
		final UserDao currUserDao = UserDao.create(this.vertx, config);
		
		serviceConsumers.add(serviceBinder
			.setAddress(UserDao.SERVICE_ADDRESS)
			.register(UserDao.class, currUserDao)
		);
		this.userDao = (UserDaoImpl)currUserDao;
		LOGGER.info("user dao service   [ OK ]");
	}
	
	private JsonObject serializeToJson(User user) 
	{
		final JsonObject json = new JsonObject();
		
		json.put("familyName", user.getFamilyName());
		json.put("givenName", user.getGivenName());
		json.put("email", user.getEmail());
		
		if (user.getId() != null && !"".equals(user.getId()))
		{
			json.put("_id", user.getId());
		}
		
		return json;
	}
}
