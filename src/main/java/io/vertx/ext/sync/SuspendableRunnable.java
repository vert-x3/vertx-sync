package io.vertx.ext.sync;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 * @deprecated This project will be removed with Quasar being effectively abandoned
 */
@Deprecated
public interface SuspendableRunnable {

  void run() throws SuspendExecution, InterruptedException;

}
