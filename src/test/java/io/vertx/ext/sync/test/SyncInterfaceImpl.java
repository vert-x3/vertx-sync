package io.vertx.ext.sync.test;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class SyncInterfaceImpl implements SyncInterface {

  private AsyncInterface asyncInterface;

  public SyncInterfaceImpl(AsyncInterface asyncInterface) {
    this.asyncInterface = asyncInterface;
  }

  @Override
  public String doSomething(String wibble) throws SuspendExecution, Throwable {
    return new MyAsync(asyncInterface).run();
  }
}
