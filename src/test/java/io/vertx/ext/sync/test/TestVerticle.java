package io.vertx.ext.sync.test;

import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;
import io.vertx.core.*;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.sync.AsyncAdaptor;
import io.vertx.ext.sync.SyncVerticle;
import io.vertx.ext.testmodel.AsyncInterface;
import io.vertx.ext.testmodel.AsyncInterfaceImpl;
import io.vertx.sync.ext.testmodel.AsyncInterfaceSync;
import io.vertx.sync.ext.testmodel.ReturnedInterfaceSync;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class TestVerticle extends SyncVerticle {

  private AsyncInterface ai;
  private AsyncInterfaceSync si;

  @Override
  @Suspendable
  public void start() {

    ai = new AsyncInterfaceImpl(vertx);
    si = new AsyncInterfaceSync(ai);

    try {

      String testName = config().getString("testName");
      Method meth = this.getClass().getDeclaredMethod(testName);
      meth.setAccessible(true);
      meth.invoke(this);

    } catch (AssertionError e) {
      e.printStackTrace();
      throw new IllegalStateException("Tests failed", e);
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalStateException("Failed to invoke test", e);
    }

  }

  @Suspendable
  protected void testContext() {
    Context ctx = Vertx.currentContext();
    assertTrue(ctx.isEventLoopContext());
  }

  @Suspendable
  protected void testMethodWithParamsAndHandlerNoReturn() {
    Thread th = Thread.currentThread();
    String res = si.methodWithParamsAndHandlerNoReturn("oranges", 23);
    assertEquals("oranges23", res);
    assertSame(Thread.currentThread(), th);
  }

  @Suspendable
  protected void testMethodWithNoParamsAndHandlerNoReturn() {
    String res = si.methodWithNoParamsAndHandlerNoReturn();
    assertEquals("wibble", res);
  }

  @Suspendable
  protected void testMethodWithParamsAndHandlerWithReturn() {
    String res = si.methodWithParamsAndHandlerWithReturn("oranges", 23);
    assertEquals("oranges23", res);
  }

  @Suspendable
  protected void testMethodWithNoParamsAndHandlerWithReturn() {
    String res = si.methodWithNoParamsAndHandlerWithReturn();
    assertEquals("wibble", res);
  }

  @Suspendable
  protected void testMethodWithParamsAndHandlerInterface() {
    ReturnedInterfaceSync returned = si.methodWithParamsAndHandlerInterface("apples", 123);
    assertNotNull(returned);
    String res = returned.methodWithParamsAndHandlerNoReturn("bananas", 100);
    assertEquals(res, "bananas100");
  }

  @Suspendable
  protected void testSleep() {
    Thread th = Thread.currentThread();
    AtomicInteger cnt = new AtomicInteger();
    vertx.setPeriodic(1, tid -> {
      assertSame(Thread.currentThread(), th);
      cnt.incrementAndGet();
    });
    assertSame(Thread.currentThread(), th);
    try {
      Strand.sleep(1000, TimeUnit.MILLISECONDS);
    } catch (Exception ignore) {
    }
    assertSame(Thread.currentThread(), th);
    System.out.println("cnt: " + cnt.get());
    assertTrue(cnt.get() > 900);
  }

  @Suspendable
  protected void testFiberHandler() {
    try {
      new AsyncAdaptor<Void>() {
        @Override
        protected void requestAsync() {
          testFiberHandler(this);
        }
      }.run();
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  @Suspendable
  protected void testFiberHandler(Handler<AsyncResult<Void>> resultHandler) {
    HttpServer server = vertx.createHttpServer(new HttpServerOptions().setPort(8080));
    server.requestHandler(fiberHandler(req -> {
      String res = si.methodWithParamsAndHandlerNoReturn("oranges", 23);
      assertEquals("oranges23", res);
      req.response().end();
    }));
    server.listen(res -> {
      assertTrue(res.succeeded());
      HttpClient client = vertx.createHttpClient(new HttpClientOptions().setDefaultPort(8080));
        client.getNow("/somepath", resp -> {
          assertTrue(resp.statusCode() == 200);
          client.close();
          server.close(res2 -> {
            resultHandler.handle(Future.succeededFuture());
          });
        });
    });
  }

  @Override
  @Suspendable
  public void stop() {

    try {

      testContext();

    } catch (AssertionError e) {
      e.printStackTrace();
      fail("tests failed");
    }

  }

}
