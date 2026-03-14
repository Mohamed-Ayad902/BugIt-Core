package com.example.core_contracts.validation

/** Marker for generic form fields. Useful for type-specific validator dispatch. */
interface FieldType

/** Special marker for image fields (attachments) */
interface ImageFieldType

data class FormField<T>(val value: T, val result: ValidationResult = ValidationResult.Valid)

sealed interface ValidationResult {
    data object Valid : ValidationResult

    sealed interface Invalid : ValidationResult {
        data object Empty : Invalid
        data class TooLong(val actualLength: Int, val maxLength: Int) : Invalid
        data class ImageTooLarge(val maxMegabytes: Int) : Invalid
        data object ImageNotSupported : Invalid
    }
}