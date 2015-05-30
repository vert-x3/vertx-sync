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

    SyncInterface si = new SyncInterfaceImpl(ai);

    new Fiber<Void>() {
      @Override
      protected Void run() throws SuspendExecution, InterruptedException {

        String res1 = null;
        try {
          res1 = si.doSomething("hello1");
        } catch (Throwable t) {
          t.printStackTrace();
          throw new RuntimeException(t);
        }

        System.out.println("Got res " + res1);

        return null;
      }
    }.start().join();
  }
}
