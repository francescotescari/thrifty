package com.microsoft.thrifty.internal

actual class AtomicInteger actual constructor(initialValue: Int) {
    private var value: Int = initialValue

    actual fun get(): Int = value

    actual fun incrementAndGet(): Int = ++value
}

actual class AtomicBoolean actual constructor(initialValue: Boolean) {
    private var value = initialValue

    actual fun get(): Boolean = value

    actual fun compareAndSet(expected: Boolean, update: Boolean): Boolean =
        if (value == expected) {
            value = update
            true
        } else {
            false
        }
}
