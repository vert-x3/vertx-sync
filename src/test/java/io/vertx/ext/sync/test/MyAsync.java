package io.vertx.ext.sync.test;

import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.SuspendExecution;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public abstract class MyAsync extends FiberAsync<String, Throwable> implements Handler<AsyncResult<String>>  {
  protected AsyncInterface ai;

  public MyAsync(AsyncInterface ai) {
    this.ai = ai;
  }

  @Override
  public void handle(AsyncResult<String> res) {
    if (res.succeeded()) {
      asyncCompleted(res.result());
    } else {
      asyncFailed(res.cause());
    }
  }
}
