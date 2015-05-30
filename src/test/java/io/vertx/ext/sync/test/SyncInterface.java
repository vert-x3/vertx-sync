package io.vertx.ext.sync.test;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public interface SyncInterface {
  String doSomething(String wibble) throws SuspendExecution, Throwable;
}
