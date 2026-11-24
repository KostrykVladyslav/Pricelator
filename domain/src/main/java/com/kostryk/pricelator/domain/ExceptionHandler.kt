package com.kostryk.pricelator.domain

import kotlin.coroutines.CoroutineContext

interface ExceptionHandler {
    suspend fun <T> handle(
        dispatcher: CoroutineContext,
        callable: suspend () -> T,
    ): AppResult<T>
}