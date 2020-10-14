package io.vertx.ext.sync.npe.service.user;

import java.io.Serializable;
import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.sync.Sync;
import io.vertx.serviceproxy.ServiceException;

public class UserDaoImpl implements UserDao, Serializable
{
	public static final String USER_COLLECTION_NAME = "user";

	private static final long serialVersionUID = 1L;
	
	private final JsonObject config;

	private final MongoClient mongoClient;
	
	/**
	 * Implementation du service W2UserDao.
	 * 
	 * @param vertx référence vers le verticle DAO.
	 * @param config configuration de l'accès à la base de données MongoDB.
	 */
	public UserDaoImpl(final Vertx vertx, final JsonObject config)
	{
		this.config = config;
		mongoClient = MongoClient.createNonShared(vertx, config);
	}
	
	@Override
	public void findAll(final Handler<AsyncResult<List<JsonObject>>> result) 
	{
		try
		{
			final List<JsonObject> results = Sync.awaitResult(h -> mongoClient.find(USER_COLLECTION_NAME, new JsonObject(), h));
			result.handle(Future.succeededFuture(results));
		}
		catch (VertxException ve)
		{
			result.handle(ServiceException.fail(400, ve.getMessage()));
		}
	}
	
	@Override
	public void loadById(final String id, final Handler<AsyncResult<JsonObject>> result)
	{
		try 
		{
			// _id
			mongoClient.findOne(USER_COLLECTION_NAME, new JsonObject().put("_id", id), new FindOptions().toJson(), new Handler<AsyncResult<JsonObject>>(){
				@Override
				public void handle(AsyncResult<JsonObject> event) 
				{
					if (event.succeeded() && event.result() != null)
					{
						final JsonObject json = event.result();
						result.handle(Future.succeededFuture(json));
					}
					else
					{
						result.handle(ServiceException.fail(403, "User " + id + " not found"));
					}
				}
			});
			
		} 
		catch (Exception e) 
		{
			result.handle(ServiceException.fail(404, "User " + id + " not found"));
		}
	}
	
	@Override
	public void close() 
	{
		mongoClient.close();
	}
}
