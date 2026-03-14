package com.example.core.feature.bug_reporting.domain.model

import com.example.core_contracts.validation.IRequestValidation
import com.example.core_contracts.validation.request.RemoteRequest

data class BugReportRequest(
    val description: String,
    val imageUriString: String,
    val dynamicFields: Map<String, String> = emptyMap()
) : IRequestValidation {

    override fun isInitialState() = description.isBlank() && imageUriString.isBlank()

    override val remoteMap: RemoteRequest
        get() = RemoteRequest(
            requestBody = hashMapOf(
                "description" to description
            )
        )
}