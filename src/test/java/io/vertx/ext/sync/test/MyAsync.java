package io.vertx.ext.sync.test;

import co.paralleluniverse.fibers.FiberAsync;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MyAsync extends FiberAsync<String, Throwable> implements Handler<AsyncResult<String>>, SyncInterface  {

  private AsyncInterface ai;

  public MyAsync(AsyncInterface ai) {
    this.ai = ai;
  }

  @Override
  protected void requestAsync() {
    ai.doSomething("hello", this);
  }

  @Override
  public void handle(AsyncResult<String> res) {
    if (res.succeeded()) {
      asyncCompleted(res.result());
    } else {
      asyncFailed(res.cause());
    }
  }

  @Override
  public String doSomething(String wibble) {

    String res;

    try {
      res = run();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }

    return res;
  }
}
