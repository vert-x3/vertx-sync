package io.vertx.ext.sync;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * A `Verticle` which runs its `start` and `stop` methods using fibers.
 *
 * You should subclass this class instead of `AbstractVerticle` to create any verticles that use vertx-sync.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 * @deprecated This project will be removed with Quasar being effectively abandoned
 */
@Deprecated
public abstract class SyncVerticle extends AbstractVerticle {

  protected FiberScheduler instanceScheduler;

  @Override
  public void start(Promise<Void> startFuture) throws Exception {
    instanceScheduler = Sync.getContextScheduler();
    new Fiber<Void>(instanceScheduler, () -> {
      try {
        SyncVerticle.this.start();
        startFuture.complete();
      } catch (Throwable t) {
        startFuture.fail(t);
      }
    }).start();
  }

  @Override
  public void stop(Promise<Void> stopFuture) throws Exception {
    new Fiber<Void>(instanceScheduler, () -> {
      try {
        SyncVerticle.this.stop();
        stopFuture.complete();
      } catch (Throwable t) {
        stopFuture.fail(t);
      } finally {
        Sync.removeContextScheduler();
      }
    }).start();
  }

  /**
   * Override this method in your verticle
   */
  @Override
  @Suspendable
  public void start() throws Exception {
  }

  /**
   * Optionally override this method in your verticle if you have cleanup to do
   */
  @Override
  @Suspendable
  public void stop() throws Exception {
  }

}
