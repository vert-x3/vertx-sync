package io.vertx.ext.sync.npe;

import static org.assertj.core.api.Assertions.assertThat;
import java.net.InetSocketAddress;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.paralleluniverse.fibers.Fiber;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.sync.Sync;
import io.vertx.ext.sync.npe.service.user.UserDao;
import io.vertx.ext.sync.npe.verticle.DaoSyncVerticleForTesting;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class UserDaoTest 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoTest.class);
	
	private static MongoServer server;
	
	private static InetSocketAddress serverAddress;
	
	private static DaoSyncVerticleForTesting verticle;
	
	private static Vertx vertx;
	
	private static MongoClient client;
	
	
	@BeforeClass
	public static void setUp(final TestContext testContext) 
	{
	    // log.info("@BeforeAll - executes once before all test methods in this class");
		server = new MongoServer(new MemoryBackend());

        // bind on a random local port
        serverAddress = server.bind();
        
        final String dbName = UserDaoTest.class.getSimpleName();
        final String host = serverAddress.getHostName();
        final int port = serverAddress.getPort();

        VertxOptions options = new VertxOptions();
 		if (java.lang.management.ManagementFactory.getRuntimeMXBean().
 			    getInputArguments().toString().indexOf("-agentlib:jdwp") > 0)
 		{
 			options.setBlockedThreadCheckInterval(1_000_000L);
 		}
        vertx = Vertx.vertx(options);
        
        verticle = new DaoSyncVerticleForTesting(dbName, host, port);
        client = MongoClient.createNonShared(vertx, verticle.createMongoConfiguration());
        
        LOGGER.info("Starting memory mongo server: " + host + ":" + port + "/" + dbName);
        
        Thread.currentThread().setName("JUNIT");
        final Async async = testContext.async();
        
        vertx.deployVerticle(verticle, new Handler<AsyncResult<String>>()
		{
			@Override
			public void handle(AsyncResult<String> deployVerticleEvent) 
			{
				if (deployVerticleEvent.succeeded())
				{
					async.complete();
				}
				else
				{
					testContext.fail(deployVerticleEvent.cause());
					async.complete();
				}
			}
		});
	}
	
	
	@AfterClass
	public static void setDown(final TestContext testContext) throws Exception
	{
		if (client != null)
		{
			client.close();
		}

		try
		{
			if (verticle != null)
			{
				verticle.stop();
			}
		}
		finally
		{
			if (server != null)
			{
				server.stopListenting();
				server.shutdownNow();
			}
		}
	}

	@Test
	public void testFindAll(final TestContext testContext) throws Exception
	{
		Async async = testContext.async();
		final UserDao userDaoProxy = UserDao.createProxy(vertx, UserDao.SERVICE_ADDRESS);
		
		new Fiber<Void>(() -> {
			final List<JsonObject> users = Sync.awaitResult(h -> userDaoProxy.findAll(h));
			
			testContext.verify(h -> {
				assertThat(Thread.currentThread().getName()).isEqualTo("JUNIT");
				assertThat(users.size()).isEqualTo(2);
				async.complete();
			});
		}).start().get();
		
	}
}
