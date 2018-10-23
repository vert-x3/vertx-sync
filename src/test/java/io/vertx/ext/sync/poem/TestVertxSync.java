package io.vertx.ext.sync.poem;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sync.Sync;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class TestVertxSync
{	
	/** Logger on the class. */
	private static Logger LOGGER = LoggerFactory.getLogger(TestVertxSync.class);
	
	private static final AtomicReference<String> VERTICLE_DEPLOYMENT_ID = new AtomicReference<String>(null);

	private static PoemVerticleForTesting VERTICLE;
	
	private static Vertx vertx;
	
	@BeforeClass
	public static void beforeAll(TestContext context) throws Exception
	{
		LOGGER.info("starting on the beforeAll() method...");
		final Async checkpointBeforeAll = context.async();
		
		vertx = Vertx.vertx();
		VERTICLE = new PoemVerticleForTesting();
		vertx.deployVerticle(VERTICLE, new Handler<AsyncResult<String>>() {
			@Override
			public void handle(AsyncResult<String> deployVerticleEvent) 
			{
				if (deployVerticleEvent.succeeded())
				{
					// attendre le succès du déploiement
					VERTICLE_DEPLOYMENT_ID.set(deployVerticleEvent.result());
					
					LOGGER.info("deploy PoemVerticleForTesting verticle    [ OK ]");
					checkpointBeforeAll.complete();
				}
				else
				{
					context.fail(deployVerticleEvent.cause());
				}
			}
		});
	}

	@AfterClass
	public static void afterAll(TestContext context)
	{
		LOGGER.info("starting on the afterAll() method...");
		String deploymentId = VERTICLE_DEPLOYMENT_ID.get();
		
		if (deploymentId != null && !"".equals(deploymentId.trim()))
		{
			final Async checkpoint = context.async();
			vertx.undeploy(deploymentId, new Handler<AsyncResult<Void>>() 
			{
				@Override
				public void handle(AsyncResult<Void> event)
				{
					LOGGER.info("undeploiement PoemVerticleForTesting verticle    [ OK ]");
					checkpoint.complete();
				}
			});	
		}
	}
	
	@Test
	public void testLoadTheDormeurDuValPoem(TestContext context) throws Exception
	{
		LOGGER.info("unit test 'testLoadTheDormeurDuValPoem' in running...");
		try
		{
			final String poem = Sync.awaitResult(h -> VERTICLE.loadTheDormeurDuValPoem());
			assertThat(poem).isNotEmpty().contains("Rimbaud");
		} 
		catch (Exception e)
		{
			context.fail(e);
		}
	}
}
