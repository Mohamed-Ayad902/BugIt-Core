package com.example.core_contracts.exceptions

import kotlin.reflect.KClass

sealed class BugItExceptions(
    message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    class ValidationException(clazz: KClass<*>, message: String? = null) :
        BugItExceptions(StringBuilder("There is missing input for this class: ${clazz.simpleName}").apply {
            message?.let { append(", message: $message") }
        }.toString())

    /** serialization or deserialization errors */
    class SerializationException(message: String?, cause: Throwable? = null) :
        BugItExceptions(message, cause)

    sealed class Network(
        open val rawBody: String? = null,
        message: String? = null,
        cause: Throwable? = null
    ) : BugItExceptions(message, cause) {

        data class Connection(override val message: String?, override val cause: Throwable?) :
            Network(null, message, cause)

        /** often shows that there is a client side validation error */
        data class BadRequest(val code: Int = 400, override val rawBody: String?, override val message: String?) : Network(rawBody, message)
        data class Unauthorized(val code: Int = 401, override val rawBody: String?, override val message: String?) : Network(rawBody, message)
        data class Forbidden(val code: Int = 403, override val rawBody: String?, override val message: String?) : Network(rawBody, message)
        data class NotFound(val code: Int = 404, override val rawBody: String?, override val message: String?) : Network(rawBody, message)
        data class Server(val code: Int, override val rawBody: String?, override val message: String?) : Network(rawBody, message)
        data class Unhandled(val code: Int, override val rawBody: String?, override val message: String?) : Network(rawBody, message)
    }

    data class Unknown(override val message: String?, override val cause: Throwable?) :
        BugItExceptions(message, cause)
}

internal class NetworkResponseException(
    val statusCode: Int,
    val body: String? = null,
    message: String? = null,
    cause: Throwable? = null
) : Exception(message ?: "HTTP $statusCode: ${body?.take(200)}", cause)