package com.kostryk.pricelator.domain

sealed interface AppError {
    data object ApiLimit : AppError
    class Http(val code: Int) : AppError
    data object Unknown : AppError
    data object None : AppError
}