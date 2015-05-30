package io.vertx.ext.sync.test;

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
  public String doSomething(String wibble) {
    return null;
  }
}
