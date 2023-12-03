package com.microsoft.thrifty.transport

internal fun unsupported(): Nothing = error("Operation not supported on JS")
actual class SocketTransport internal actual constructor(builder: Builder) :
    Transport {
    actual class Builder actual constructor(host: String, port: Int) {
        /**
         * The number of milliseconds to wait for a connection to be established.
         */
        actual fun connectTimeout(connectTimeout: Int): Builder {
            unsupported()
        }

        /**
         * The number of milliseconds a read operation should wait for completion.
         */
        actual fun readTimeout(readTimeout: Int): Builder {
            unsupported()
        }

        /**
         * Enable TLS for this connection.
         */
        actual fun enableTls(enableTls: Boolean): Builder {
            unsupported()
        }

        actual fun build(): SocketTransport {
            unsupported()
        }

    }

    actual fun connect() {
        unsupported()
    }

    override fun read(buffer: ByteArray, offset: Int, count: Int): Int {
        unsupported()
    }

    override fun write(buffer: ByteArray, offset: Int, count: Int) {
        unsupported()
    }

    override fun close() {
        unsupported()
    }

    override suspend fun flush() {
        unsupported()
    }
}