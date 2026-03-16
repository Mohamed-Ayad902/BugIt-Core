package com.example.core_contracts.validation

/**
 * Validates the bug description.
 * @param maxLength Default is 1000 characters.
 */
fun String.validateDescription(maxLength: Int = 1000): ValidationResult {
    return when {
        this.isBlank() -> ValidationResult.Invalid.Empty
        this.length > maxLength -> ValidationResult.Invalid.TooLong(this.length, maxLength)
        else -> ValidationResult.Valid
    }
}

fun String.validateImageUri(): ValidationResult {
    return when {
        this.isBlank() -> ValidationResult.Invalid.Empty
        else -> ValidationResult.Valid
    }
}