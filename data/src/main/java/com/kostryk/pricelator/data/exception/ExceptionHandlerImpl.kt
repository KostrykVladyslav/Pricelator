package com.kostryk.pricelator.data.exception

import com.kostryk.pricelator.domain.AppError
import com.kostryk.pricelator.domain.AppResult
import com.kostryk.pricelator.domain.ExceptionHandler
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class ExceptionHandlerImpl : ExceptionHandler {

    override suspend fun <T> handle(
        dispatcher: CoroutineContext,
        callable: suspend () -> T,
    ): AppResult<T> {
        return try {
            val result = withContext(dispatcher) {
                callable()
            }
            AppResult.Success(result)
        } catch (e: HttpException) {
            when (e.code()) {
                429 -> AppResult.Error(AppError.ApiLimit)
                in 400..499 -> AppResult.Error(AppError.Http(e.code()))
                in 500..599 -> AppResult.Error(AppError.Http(e.code()))
                else -> AppResult.Error(AppError.Unknown)
            }
        } catch (e: IOException) {
            AppResult.Error(AppError.Unknown)
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown)
        }
    }
}
