package io.vertx.ext.sync;

import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public abstract class SyncVerticle extends AbstractVerticle {

  protected FiberScheduler instanceScheduler;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    instanceScheduler = Sync.getContextScheduler();
    Sync.runOnFiber(instanceScheduler, new Runnable() {
      // TODO - for some reason this does not work if we use a lambda instead of anonymous class
      @Override
      @Suspendable
      public void run() {
        try {
          SyncVerticle.this.start();
          startFuture.complete();
        } catch (Throwable t) {
          startFuture.fail(t);
        }
      }
    });
  }

  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    Sync.runOnFiber(instanceScheduler,  new Runnable() {
      // TODO - for some reason this does not work if we use a lambda instead of anonymous class
      @Override
      @Suspendable
      public void run() {
        try {
          SyncVerticle.this.stop();
          stopFuture.complete();
        } catch (Throwable t) {
          stopFuture.fail(t);
        } finally {
          Sync.removeContextScheduler();
        }
      }
    });
  }


  @Override
  @Suspendable
  public void start() throws Exception {
  }

  @Override
  @Suspendable
  public void stop() throws Exception {
  }

}
