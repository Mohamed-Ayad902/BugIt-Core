package com.example.core_contracts.data_source.remote

import java.io.File
import java.lang.reflect.Type

interface INetworkProvider {

    suspend fun <ResponseBody, RequestBody> post(
        responseWrappedModel: Type, pathUrl: String, headers: Map<String, Any>? = null,
        queryParams: Map<String, Any>? = null, requestBody: RequestBody? = null
    ): ResponseBody

    suspend fun <ResponseBody> get(
        responseWrappedModel: Type, pathUrl: String, headers: Map<String, Any>? = null,
        queryParams: Map<String, Any>? = null
    ): ResponseBody

    suspend fun <ResponseBody, RequestBody> postWithImagesFile(
        responseWrappedModel: Type,
        pathUrl: String,
        headers: Map<String, Any>? = null,
        queryParams: Map<String, Any>? = null,
        requestBody: RequestBody?,
        files: HashMap<String, File>
    ): ResponseBody

    suspend fun <ResponseBody, RequestBody> delete(
        responseWrappedModel: Type, pathUrl: String, headers: Map<String, Any>? = null,
        queryParams: Map<String, Any>? = null, requestBody: RequestBody? = null
    ): ResponseBody
}