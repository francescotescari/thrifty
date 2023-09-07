package com.microsoft.thrifty.service

import com.microsoft.thrifty.Struct
import com.microsoft.thrifty.protocol.Protocol
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okio.Closeable
import okio.IOException

actual open class AsyncClientBase protected actual constructor(
    protocol: Protocol,
    private val listener: Listener
) : ClientBase(protocol), Closeable {

    private val pendingCalls = Channel<MethodCall<*>>(Channel.UNLIMITED)
    private val workerCoroutine = GlobalScope.launch {
        try {
            for (call in pendingCalls) {
                var result: Any? = null
                var error: Exception? = null
                try {
                    result = this@AsyncClientBase.invokeRequest(call)
                } catch (e: IOException) {
                    fail(call, e)
                    throw e
                } catch (e: RuntimeException) {
                    fail(call, e)
                    throw e
                } catch (e: ServerException) {
                    error = e.thriftException
                } catch (e: Exception) {
                    error = if (e is Struct) {
                        e
                    } else {
                        // invokeRequest should only throw one of the caught Exception types or
                        // an Exception extending Struct from MethodCall
                        throw AssertionError("Unexpected exception", e)
                    }
                }

                if (error != null) {
                    fail(call, error)
                } else {
                    complete(call, result)
                }
            }
        } catch (exception: Exception) {
            close(exception)
        }
    }

    /**
     * Exposes important events in the client's lifecycle.
     */
    actual interface Listener {
        /**
         * Invoked when the client connection has been closed.
         *
         * After invocation, the client is no longer usable.  All subsequent
         * method call attempts will result in an immediate exception on the
         * calling thread.
         */
        actual fun onTransportClosed()

        /**
         * Invoked when a client-level error has occurred.
         *
         * This generally indicates a connectivity or protocol error,
         * and is distinct from errors returned as part of normal service
         * operation.
         *
         * The client is guaranteed to have been closed and shut down
         * by the time this method is invoked.
         *
         * @param error the throwable instance representing the error.
         */
        actual fun onError(error: Throwable)
    }

    /**
     * Enqueues a method call for asynchronous execution.
     *
     * WARNING:
     * This method is *NOT* part of the public API.  It is an implementation
     * detail, for use by generated code only.  As multi-platform code evolves,
     * expect this to change and/or be removed entirely!
     */
    protected actual fun enqueue(methodCall: MethodCall<*>) {
        pendingCalls.trySend(methodCall)
    }

    private fun close(error: Throwable?) {
        if (!running.compareAndSet(true, false)) {
            return
        }
        pendingCalls.close()
        GlobalScope.launch {
            workerCoroutine.cancelAndJoin()
            closeProtocol()
            val incompleteCalls = mutableListOf<MethodCall<*>>()
            for (call in pendingCalls) {
                incompleteCalls.add(call)
            }
            val e = CancellationException()
            for (call in incompleteCalls) {
                try {
                    fail(call, e)
                } catch (ignored: Exception) {
                    // nope
                }
            }
            if (error != null) {
                listener.onError(error)
            } else {
                listener.onTransportClosed()
            }
        }
    }


    private fun complete(call: MethodCall<*>, result: Any?) {
        (call.callback as ServiceMethodCallback<Any?>).onSuccess(result)
    }

    private fun fail(call: MethodCall<*>, error: Throwable) {
        call.callback!!.onError(error)
    }

    override fun close() {
        close(null)
    }

}
