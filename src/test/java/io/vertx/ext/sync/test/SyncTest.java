package io.vertx.ext.sync.test;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

/**
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class SyncTest extends VertxTestBase {

  @Test
  public void testMethodWithParamsAndHandlerNoReturn() throws Exception {
    runTest(getMethodName());
  }

  @Test
  public void testMethodWithNoParamsAndHandlerNoReturn() throws Exception {
    runTest(getMethodName());
  }

  @Test
  public void testMethodWithParamsAndHandlerWithReturn() throws Exception {
    runTest(getMethodName());
  }

  @Test
  public void testMethodWithNoParamsAndHandlerWithReturn() throws Exception {
    runTest(getMethodName());
  }

  @Test
  public void testMethodWithParamsAndHandlerInterface() throws Exception {
    runTest(getMethodName());
  }

  @Test
  public void testSleep() throws Exception {
    runTest(getMethodName());
  }

  @Test
  public void testFiberHandler() throws Exception {
    runTest(getMethodName());
  }

  protected void runTest(String testName) throws Exception {

    vertx.deployVerticle(new TestVerticle(),
      new DeploymentOptions().setConfig(new JsonObject().put("testName", testName)), res -> {
      if (res.succeeded()) {
        vertx.undeploy(res.result(), res2 -> {
          if (res2.succeeded()) {
            testComplete();
          } else {
            res2.cause().printStackTrace();
            fail("Failure in undeploying");
          }
        });
      } else {
        res.cause().printStackTrace();
        fail("Failure in running tests");
      }
    });

    await();
  }

  protected String getMethodName() {
    return Thread.currentThread().getStackTrace()[2].getMethodName();
  }


}
