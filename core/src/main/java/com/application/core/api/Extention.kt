package com.application.core.api

import com.application.core.Result
import retrofit2.Response

suspend fun <T : Any> safeApiCall(
    call: suspend () -> Result<T>,
): Result<T> {
    return try {
        call()
    } catch (e: Exception) {
        // An exception was thrown when calling the API so we're converting this to an IOException
        e.printStackTrace()
        Result.Error(e)
    }
}

fun <T : Any> Response<T>.processResponse(): Result<T> {
    return when {
        this.isSuccessful -> {
            this.body()?.let {
                Result.Success(it)
            } ?: run {
                Result.Error(Exception(this.message()))
            }
        }

        else -> Result.Error(
            Exception(this.message())
        )
    }
}
