package com.example.core_contracts.exceptions

import kotlinx.coroutines.CancellationException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

internal object ExceptionCatcher {
    fun map(throwable: Throwable): BugItExceptions {
        if (throwable is CancellationException) throw throwable

        return when (throwable) {
            is NetworkResponseException -> mapNetworkResponse(throwable)

            is SocketTimeoutException -> BugItExceptions.Network.Connection(throwable.message ?: "Connection timed out", throwable)
            is ConnectException, is IOException -> BugItExceptions.Network.Connection(throwable.message ?: "Network error", throwable)
            is IllegalStateException -> BugItExceptions.ValidationException(Any::class, throwable.message)
            is BugItExceptions -> throwable

            else -> BugItExceptions.Unknown(throwable.message, throwable)
        }
    }

    private fun mapNetworkResponse(ex: NetworkResponseException): BugItExceptions.Network {
        return when (ex.statusCode) {
            400 -> BugItExceptions.Network.BadRequest(ex.statusCode, ex.body, ex.message)
            401 -> BugItExceptions.Network.Unauthorized(ex.statusCode, ex.body, ex.message)
            403 -> BugItExceptions.Network.Forbidden(ex.statusCode, ex.body, ex.message)
            404 -> BugItExceptions.Network.NotFound(ex.statusCode, ex.body, ex.message)
            in 500..599 -> BugItExceptions.Network.Server(ex.statusCode, ex.body, ex.message)
            else -> BugItExceptions.Network.Unhandled(ex.statusCode, ex.body, ex.message)
        }
    }
}

fun BugItExceptions.extractErrorDto(): ErrorDto? {
    val rawToParse = when (this) {
        is BugItExceptions.Network -> rawBody ?: message
        else -> message
    }
    return ErrorDto.fromRaw(rawToParse) ?: ErrorDto.fromRaw(this.cause?.message)
}