package io.vertx.ext.sync;

import co.paralleluniverse.fibers.*;
import io.vertx.core.*;

/**
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public abstract class SyncVerticle extends AbstractVerticle {

  protected FiberScheduler instanceScheduler;
  protected Thread eventLoop;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    if (context.isWorkerContext()) {
      throw new IllegalStateException("Vert.x sync can only be used with standard verticles");
    }
    eventLoop = Thread.currentThread();
    instanceScheduler = new FiberExecutorScheduler("mine", command -> {
      if (Thread.currentThread() != eventLoop) {
        context.runOnContext(v -> command.run());
      } else {
        // Just run directly
        command.run();
      }
    });
    new Fiber<Void>(instanceScheduler) {
      @Override
      protected Void run() throws SuspendExecution, InterruptedException {
        try {
          SyncVerticle.this.start();
          startFuture.complete();
        } catch (Throwable t) {
          startFuture.fail(t);
        }
        return null;
      }
    }.start();
  }

  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    new Fiber<Void>(instanceScheduler) {
      @Override
      protected Void run() throws SuspendExecution, InterruptedException {
        try {
          SyncVerticle.this.stop();
          stopFuture.complete();
        } catch (Throwable t) {
          stopFuture.fail(t);
        }
        return null;
      }
    }.start();
  }

  private void runOnFiber(Runnable runner) {
    new Fiber<Void>(instanceScheduler) {
      @Override
      protected Void run() throws SuspendExecution, InterruptedException {
        runner.run();
        return null;
      }
    }.start();
  }

  public <T> Handler<T> fiberHandler(Handler<T> handler) {
    return p -> runOnFiber(() -> handler.handle(p));
  }

  @Override
  @Suspendable
  public void start() {
  }

  @Override
  @Suspendable
  public void stop() {
  }

}
