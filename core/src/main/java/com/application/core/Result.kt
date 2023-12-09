package com.application.core

sealed class Result<out T : Any> {

    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] and [isLoading] returns `false`.
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] and [isLoading] returns `false`.
     */
    val isFailure: Boolean get() = this is Error

    /**
     * Returns `true` if this instance represents a Loading outcome.
     * In this case [isSuccess] and [isFailure] returns `false`.
     */
    val isLoading: Boolean get() = this is Loading

    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
