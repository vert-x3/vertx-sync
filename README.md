# Synchronous but non-OS-thread-blocking verticles

Hate blocking OS threads?

Love scalability?

Hate callback hell?

Well now you can have your cake and eat it...

vert-sync enables you to write verticles that interact with services in a direct blocking style but which don't
actually block any OS threads.

Note: Vertx-sync is still very much a work in progress

## SyncVerticle

Vert-sync only works with Java currently.

In order to use vertx-sync you must deploy your code as instances of `io.vertx.ext.sync.SyncVerticle`.

You should override the `start()` and (optionally) the `stop()` methods of the verticle.

Those methods must be annotated with the `@Suspendable` annotation.

## Generating sync versions of CodeGen interfaces

vertx-sync will create sync versions of any methods in `@VertxGen` interfaces where the last parameter is of type
`Handler<AsyncResult<T>>`




