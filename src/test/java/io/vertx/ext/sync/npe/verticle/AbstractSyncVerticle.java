package io.vertx.ext.sync.npe.verticle;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.sync.Sync;
import io.vertx.ext.sync.SyncVerticle;

public abstract class AbstractSyncVerticle extends SyncVerticle
{
	@Override
	public void start(final Future<Void> startFuture) throws Exception 
	{
		final Future<Void> onStartingFuture = Future.future();
		onStartingFuture.setHandler(new Handler<AsyncResult<Void>>() 
		{
			@Override
			public void handle(final AsyncResult<Void> onStartingFutureEvent) 
			{
				if (onStartingFutureEvent.succeeded())
				{
					startFuture.complete();
				}
				else
				{
					startFuture.fail(onStartingFutureEvent.cause());
				}
			}
		});		
		
		instanceScheduler = Sync.getContextScheduler();
		new Fiber<Void>(instanceScheduler, () -> {
			try 
			{
				onStarting(onStartingFuture);
			} 
			catch (final Throwable t) 
			{
				startFuture.fail(t);
			}
		}).start();
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception 
	{
		final Future<Void> onStopingFuture = Future.future();
		onStopingFuture.setHandler(new Handler<AsyncResult<Void>>() 
		{
			@Override
			public void handle(final AsyncResult<Void> onStopingFutureEvent) 
			{
				if (onStopingFutureEvent.succeeded())
				{
					stopFuture.complete();
				}
				else
				{
					stopFuture.fail(onStopingFutureEvent.cause());
				}
			}
		});		
		
		new Fiber<Void>(instanceScheduler, () -> {
			try 
			{
				onStoping(onStopingFuture);
			} 
			catch (final Throwable t) 
			{
				stopFuture.fail(t);
			} 
			finally 
			{
				Sync.removeContextScheduler();
			}
		}).start();
	}

	/**
	 * Override this method in your verticle
	*/
	@Override
	@Suspendable
	public final void start() throws Exception 
	{
		// do nothing
	}
	
	/**
	 * Optionally override this method in your verticle if you have cleanup to do
	 */
	@Override
	@Suspendable
	public final void stop() throws Exception 
	{
		// do nothing
	}
	
	@Suspendable
	protected void onStarting(final Future<Void> onStartingFuture)
	{
		onStartingFuture.complete();
	}

	@Suspendable
	protected void onStoping(final Future<Void> onStopingFuture)
	{
		onStopingFuture.complete();
	}
}
