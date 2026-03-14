package com.example.core_contracts.validation

/**
 * Defines a basic check to see if a request object is in its
 * default/empty state before proceeding with validation or execution.
 */
fun interface IRequestValidation {
    fun isInitialState(): Boolean
}