package com.microsoft.thrifty.transport

actual class HttpTransport actual constructor(url: String) : Transport {
    actual fun setConnectTimeout(timeout: Int) {
        unsupported()
    }

    actual fun setReadTimeout(timeout: Int) {
        unsupported()
    }

    actual fun setCustomHeaders(headers: Map<String, String>) {
        unsupported()
    }

    actual fun setCustomHeader(key: String, value: String) {
        unsupported()
    }

    override fun close() {
        unsupported()
    }

    override fun read(buffer: ByteArray, offset: Int, count: Int): Int {
        unsupported()
    }

    override fun write(buffer: ByteArray, offset: Int, count: Int) {
        unsupported()
    }

    override suspend fun flush() {
        unsupported()
    }
}