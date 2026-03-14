package com.example.core.feature.bug_reporting.domain.model

data class Bug(
    val id: String,
    val description: String,
    val screenshotUri: String,
    val dynamicFields: Map<String, String>,
    val createdAt: String
)