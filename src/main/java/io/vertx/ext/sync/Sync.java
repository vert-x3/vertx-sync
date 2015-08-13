package io.vertx.ext.sync;

import co.paralleluniverse.fibers.*;
import co.paralleluniverse.strands.channels.Channel;
import io.vertx.core.*;
import io.vertx.ext.sync.impl.AsyncAdaptor;
import io.vertx.ext.sync.impl.HandlerAdaptor;
import io.vertx.ext.sync.impl.HandlerReceiverAdaptorImpl;

import java.util.function.Consumer;

/**
 * This class contains various static methods to allowing events and asynchronous results to be accessed
 * in a synchronous way.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Sync {

  private static final String FIBER_SCHEDULER_CONTEXT_KEY = "__vertx-sync.fiberScheduler";

  /**
   * Invoke an asynchronous operation and obtain the result synchronous.
   * The fiber will be blocked until the result is available. No kernel thread is blocked.
   *
   * @param consumer  this should encapsulate the asynchronous operation. The handler is passed to it.
   * @param <T>  the type of the result
   * @return  the result
   */
  @Suspendable
  public static <T> T syncResult(Consumer<Handler<AsyncResult<T>>> consumer) {
    try {
      return new AsyncAdaptor<T>() {
        @Override
        protected void requestAsync() {
          try {
            consumer.accept(this);
          } catch (Exception e) {
            throw new VertxException(e);
          }
        }
      }.run();
    } catch (Throwable t) {
      throw new VertxException(t);
    }
  }

  /**
   * Receive a single event from a handler synchronously.
   * The fiber will be blocked until the event occurs. No kernel thread is blocked.
   *
   * @param consumer  this should encapsulate the setting of the handler to receive the event. The handler is passed to it.
   * @param <T>  the type of the event
   * @return  the event
   */
  @Suspendable
  public static <T> T syncEvent(Consumer<Handler<T>> consumer) {
    try {
      return new HandlerAdaptor<T>() {
        @Override
        protected void requestAsync() {
          try {
            consumer.accept(this);
          } catch (Exception e) {
            throw new VertxException(e);
          }
        }
      }.run();
    } catch (Throwable t) {
      throw new VertxException(t);
    }
  }

  /**
   * Convert a standard handler to a handler which runs on a fiber. This is necessary if you want to do fiber blocking
   * synchronous operations in your handler.
   *
   * @param handler  the standard handler
   * @param <T>  the event type of the handler
   * @return  a wrapped handler that runs the handler on a fiber
   */
  @Suspendable
  public static <T> Handler<T> fiberHandler(Handler<T> handler) {
    return p -> runOnFiber(getContextScheduler(), () -> handler.handle(p));
  }

  /**
   * Create an adaptor that converts a stream of events from a handler into a receiver which allows the events to be
   * received synchronously.
   *
   * @param <T>  the type of the event
   * @return  the adaptor
   */
  @Suspendable
  public static <T> HandlerReceiverAdaptor<T> streamAdaptor() {
    return new HandlerReceiverAdaptorImpl<>(getContextScheduler());
  }

  /**
   * Like {@link #streamAdaptor()} but using the specified Quasar `Channel` instance. This is useful if you want to
   * fine-tune the behaviour of the adaptor.
   *
   * @param channel  the Quasar channel
   * @param <T>  the type of the event
   * @return  the adaptor
   */
  @Suspendable
  public static <T> HandlerReceiverAdaptor<T> streamAdaptor(Channel<T> channel) {
    return new HandlerReceiverAdaptorImpl<>(getContextScheduler(), channel);
  }

  /**
   * Run the specified action on a fiber
   *
   * @param fiberScheduler  the schedulre to use
   * @param runner  the action
   */
  @Suspendable
  public static void runOnFiber(FiberScheduler fiberScheduler, Runnable runner) {
    new Fiber<Void>(fiberScheduler) {
      @Override
      protected Void run() throws SuspendExecution, InterruptedException {
        runner.run();
        return null;
      }
    }.start();
  }

  /**
   * Get the `FiberScheduler` for the current context. There should be only one instance per context.
   * @return  the scheduler
   */
  @Suspendable
  public static FiberScheduler getContextScheduler() {
    Context context = Vertx.currentContext();
    if (context == null) {
      throw new IllegalStateException("Not in context");
    }
    if (!context.isEventLoopContext()) {
      throw new IllegalStateException("Not on event loop");
    }
    // We maintain one scheduler per context
    FiberScheduler scheduler = context.get(FIBER_SCHEDULER_CONTEXT_KEY);
    if (scheduler == null) {
      Thread eventLoop = Thread.currentThread();
      scheduler = new FiberExecutorScheduler("mine", command -> {
        if (Thread.currentThread() != eventLoop) {
          context.runOnContext(v -> command.run());
        } else {
          // Just run directly
          command.run();
        }
      });
      context.put(FIBER_SCHEDULER_CONTEXT_KEY, scheduler);
    }
    return scheduler;
  }

  /**
   * Remove the scheduler for the current context
   */
  @Suspendable
  public static void removeContextScheduler() {
    Context context = Vertx.currentContext();
    if (context != null) {
      context.remove(FIBER_SCHEDULER_CONTEXT_KEY);
    }
  }


}
