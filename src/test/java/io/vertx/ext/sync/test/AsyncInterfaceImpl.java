package io.vertx.ext.sync.test;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.Future;

/**
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class AsyncInterfaceImpl implements AsyncInterface {

  private final Vertx vertx;

  public AsyncInterfaceImpl(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void doSomething(String wibble, final Handler<AsyncResult<String>> resultHandler) {
    vertx.setTimer(1000, tid -> resultHandler.handle(Future.succeededFuture(wibble)));
  }
}
