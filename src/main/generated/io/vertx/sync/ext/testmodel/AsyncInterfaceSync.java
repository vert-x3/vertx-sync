/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.sync.ext.testmodel;

import io.vertx.ext.testmodel.ReturnedInterface;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.testmodel.AsyncInterface;
import io.vertx.ext.sync.AsyncAdaptor;
import co.paralleluniverse.fibers.Suspendable;
/**
 *
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.testmodel.AsyncInterface original} non interface using Vert.x codegen.
 */

public class AsyncInterfaceSync {

  private final AsyncInterface delegate;

  public AsyncInterfaceSync(AsyncInterface delegate) {
    this.delegate = delegate;
  }

  public AsyncInterface asyncDel() {
    return delegate;
  }

  // The sync methods

  @Suspendable
  public String methodWithParamsAndHandlerNoReturn(String foo, long bar) {
    try {
      return new AsyncAdaptor<String>() {
        @Override
        protected void requestAsync() {
          delegate.methodWithParamsAndHandlerNoReturn(foo, bar, this);
        }
      }.run();
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  @Suspendable
  public String methodWithNoParamsAndHandlerNoReturn() {
    try {
      return new AsyncAdaptor<String>() {
        @Override
        protected void requestAsync() {
          delegate.methodWithNoParamsAndHandlerNoReturn(this);
        }
      }.run();
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  @Suspendable
  public String methodWithParamsAndHandlerWithReturn(String foo, long bar) {
    try {
      return new AsyncAdaptor<String>() {
        @Override
        protected void requestAsync() {
          delegate.methodWithParamsAndHandlerWithReturn(foo, bar, this);
        }
      }.run();
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  @Suspendable
  public String methodWithNoParamsAndHandlerWithReturn() {
    try {
      return new AsyncAdaptor<String>() {
        @Override
        protected void requestAsync() {
          delegate.methodWithNoParamsAndHandlerWithReturn(this);
        }
      }.run();
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  @Suspendable
  public io.vertx.sync.ext.testmodel.ReturnedInterfaceSync methodWithParamsAndHandlerInterface(String foo, long bar) {
    try {
      return new io.vertx.sync.ext.testmodel.ReturnedInterfaceSync(new AsyncAdaptor<ReturnedInterface>() {
        @Override
        protected void requestAsync() {
          delegate.methodWithParamsAndHandlerInterface(foo, bar, this);
        }
      }.run());
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

}
