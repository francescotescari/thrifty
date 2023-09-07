package com.microsoft.thrifty.internal

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okio.IOException

actual class ProtocolException actual constructor(message: String) : IOException()

actual val DefaultDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default
