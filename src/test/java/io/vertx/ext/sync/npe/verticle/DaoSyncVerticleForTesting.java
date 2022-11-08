package io.vertx.ext.sync.npe.verticle;

import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public class DaoSyncVerticleForTesting extends DaoSyncVerticle 
{
	private final String dbName;
	
	private final String serverHostName;
	
	private final int serverPort;

	public DaoSyncVerticleForTesting(final String dbName, final String serverHostName, final int serverPort) 
	{
		super();
		this.dbName = dbName;
		this.serverHostName = serverHostName;
		this.serverPort = serverPort;
	}
	
	@Override
	public JsonObject createMongoConfiguration() 
	{
		return new JsonObject()
			.put("connection_string", "mongodb://" + serverHostName + ":" + serverPort)
		    .put("db_name", dbName + System.currentTimeMillis());
	}
	
	@Suspendable
	@Override
	protected void afterMongoStarting(final Handler<AsyncResult<Void>> result) 
	{
		result.handle(Future.succeededFuture());
	}
}
