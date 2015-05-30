package io.vertx.ext.sync.test;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import io.vertx.core.Vertx;
import org.junit.Test;

/**
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class SyncAdaptorTest {

  @Test
  public void testSync() throws Exception {

    Vertx vertx = Vertx.vertx();

    AsyncInterface ai = new AsyncInterfaceImpl(vertx);

    SyncInterface si = new MyAsync(ai);

    new Fiber<Void>() {
      @Override
      protected Void run() throws SuspendExecution, InterruptedException {

        String res1 = si.doSomething("hello1");

        System.out.println("Got res " + res1);

        return null;
      }
    }.start();

    Thread.sleep(100000);


  }
}
