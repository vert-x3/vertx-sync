package io.vertx.ext.sync.test;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public interface AsyncInterface {

  void doSomething(String wibble, Handler<AsyncResult<String>> resultHandler);
}
