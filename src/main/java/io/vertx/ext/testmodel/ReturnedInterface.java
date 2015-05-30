package io.vertx.ext.testmodel;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@VertxGen
public interface ReturnedInterface {

  void methodWithParamsAndHandlerNoReturn(String foo, long bar, Handler<AsyncResult<String>> resultHandler);

}
