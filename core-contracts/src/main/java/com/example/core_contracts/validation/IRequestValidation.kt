package com.example.core_contracts.validation

import com.example.core_contracts.validation.request.RemoteRequest

/**
 * Defines a basic check to see if a request object is in its
 * default/empty state before proceeding with validation or execution.
 */
interface IRequestValidation {
    fun isInitialState(): Boolean
    val remoteMap: RemoteRequest
}