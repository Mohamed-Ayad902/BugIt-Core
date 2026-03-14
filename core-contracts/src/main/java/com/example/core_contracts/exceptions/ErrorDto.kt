package com.example.core_contracts.exceptions

import org.json.JSONArray
import org.json.JSONObject

/**
 * A Data Transfer Object (DTO) designed to parse and normalize error responses from various API formats.
 *
 * It handles standard JSON error structures, nested error objects, and provides a fallback for
 * plain-text responses or status code extraction via regex.
 *
 * @property code An optional machine-readable error identifier (e.g., "404", "AUTH_FAILED").
 * @property message An optional human-readable description of the error.
 * @property errors A map representing field-specific validation errors, where each key is a field
 *                  name and the value is a list of error descriptions.
 */
data class ErrorDto(
    val code: String? = null,
    val message: String? = null,
    val errors: Map<String, List<String>>? = null,
) {
    /**
     * Provides a flattened, human-readable summary of the error.
     *
     * Priority is given to the top-level [message]. If null, it constructs a string
     * from the [errors] map in the format: "field1: err1,err2; field2: err1".
     */
    val readableMessage: String?
        get() = message ?: errors?.entries?.joinToString("; ") { (k, v) -> "$k: ${v.joinToString(",")}" }

    companion object {
        private val CODE_KEYS = listOf("errorCode", "error_code", "code", "status", "statusCode")
        private val MESSAGE_KEYS = listOf("message", "errorMessage", "msg", "error")

        /**
         * The primary entry point for error parsing.
         *
         * Logic sequence:
         * 1. Attempt JSON parsing (handles nested "error" objects and "errors" maps).
         * 2. If JSON fails or is empty, attempt regex extraction for codes in plain text.
         *
         * @param raw The raw response body from the server.
         * @return A populated [ErrorDto] if any meaningful data is found, otherwise null.
         */
        fun fromRaw(raw: String?): ErrorDto? {
            if (raw.isNullOrBlank()) return null

            return parseFromJson(raw) ?: parseFromPlainText(raw)
        }

        /**
         * Internal JSON parser that probes for common error keys and nested structures.
         */
        private fun parseFromJson(raw: String): ErrorDto? {
            return try {
                val json = JSONObject(raw)
                val code = findFirstValue(json, CODE_KEYS)
                val message = findFirstValue(json, MESSAGE_KEYS)
                val errorsMap = parseErrorsMap(json.optJSONObject("errors"))

                // Handle legacy or wrapped error objects (e.g., { "error": { "code": "..." } })
                if (code == null && message == null && json.has("error")) {
                    val nested = json.optJSONObject("error")
                    if (nested != null) {
                        return ErrorDto(
                            code = findFirstValue(nested, CODE_KEYS),
                            message = findFirstValue(nested, MESSAGE_KEYS),
                            errors = errorsMap
                        )
                    }
                }

                if (code != null || message != null || errorsMap != null) {
                    ErrorDto(code, message, errorsMap)
                } else if (raw.trim().length <= 400) {
                    // Fallback: If it's valid JSON but doesn't match our keys, treat the whole body as the message
                    ErrorDto(null, raw.trim(), null)
                } else null
            } catch (_: Exception) {
                null // Not a JSON structure
            }
        }

        /**
         * Helper to find the first non-blank value for a list of possible JSON keys.
         */
        private fun findFirstValue(json: JSONObject, keys: List<String>): String? =
            keys.firstNotNullOfOrNull { key -> json.optString(key).takeIf { it.isNotBlank() } }

        /**
         * Parses a JSON object into a validation error map.
         * Handles both single string values and arrays of strings per key.
         */
        private fun parseErrorsMap(errorsObj: JSONObject?): Map<String, List<String>>? {
            if (errorsObj == null) return null
            val map = mutableMapOf<String, List<String>>()
            errorsObj.keys().forEach { key ->
                map[key] = when (val value = errorsObj.opt(key)) {
                    is JSONArray -> List(value.length()) { i -> value.optString(i) }
                    else -> listOf(value?.toString() ?: "")
                }
            }
            return map.takeIf { it.isNotEmpty() }
        }

        /**
         * Fallback parser using regex to extract error codes/status from unstructured text.
         * Example: extracts "403" from "Access Denied. Status: 403".
         */
        private fun parseFromPlainText(raw: String): ErrorDto? {
            val regex = Regex("""(?i)(error[_\- ]?code|code|status)[\s:]*([0-9]{1,4})""")
            return regex.find(raw)?.let { match ->
                val code = match.groupValues.getOrNull(2)
                val remainder = raw.replace(match.value, "").trim().takeIf { it.isNotBlank() }
                ErrorDto(code, remainder)
            }
        }
    }
}