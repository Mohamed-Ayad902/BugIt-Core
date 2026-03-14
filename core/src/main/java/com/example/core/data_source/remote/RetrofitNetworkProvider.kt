package com.example.core.data_source.remote

import com.example.core.extensions.getModelFromJSON
import com.example.core.extensions.toJson
import com.example.core_contracts.data_source.remote.INetworkProvider
import com.example.core_contracts.exceptions.NetworkResponseException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.lang.reflect.Type

internal class RetrofitNetworkProvider(private val apiService: ApiService) : INetworkProvider {

    override suspend fun <ResponseBody, RequestBody> delete(
        responseWrappedModel: Type, pathUrl: String, headers: Map<String, Any>?,
        queryParams: Map<String, Any>?, requestBody: RequestBody?
    ): ResponseBody = executeSafe {
        val response = apiService.delete(
            pathUrl = pathUrl, headerMap = headers ?: hashMapOf(),
            queryParams = queryParams ?: hashMapOf(), body = requestBody ?: Unit
        )
        response.string().getModelFromJSON(responseWrappedModel)
    }

    override suspend fun <ResponseBody, RequestBody> post(
        responseWrappedModel: Type, pathUrl: String, headers: Map<String, Any>?,
        queryParams: Map<String, Any>?, requestBody: RequestBody?
    ): ResponseBody = executeSafe {
        val response = apiService.post(
            pathUrl = pathUrl, headerMap = headers ?: hashMapOf(),
            queryParams = queryParams ?: hashMapOf(), body = requestBody ?: Unit
        )
        response.string().getModelFromJSON(responseWrappedModel)
    }

    override suspend fun <ResponseBody> get(
        responseWrappedModel: Type, pathUrl: String, headers: Map<String, Any>?,
        queryParams: Map<String, Any>?
    ): ResponseBody = executeSafe {
        val response = apiService.get(
            pathUrl = pathUrl, headerMap = headers ?: hashMapOf(),
            queryParams = queryParams ?: hashMapOf()
        )
        response.string().getModelFromJSON(responseWrappedModel)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <ResponseBody, RequestBody> postWithImagesFile(
        responseWrappedModel: Type,
        pathUrl: String,
        headers: Map<String, Any>?,
        queryParams: Map<String, Any>?,
        requestBody: RequestBody?,
        files: HashMap<String, File>
    ): ResponseBody = executeSafe {
        // Convert HashMap to MultipartBody.Part list
        val multipartFiles = files.map { (attributeName, file) ->
            file.asMultipart(attributeName)
        }

        // Convert request body to JSON
        val jsonRequestBody = requestBody?.toJson()
            ?.toRequestBody("application/json".toMediaTypeOrNull())

        // Convert headers & query params to Map<String, String>
        val headersString = headers?.mapValues { it.value.toString() } ?: emptyMap()
        val queryParamsString = queryParams?.mapValues { it.value.toString() } ?: emptyMap()

        // Make API call with multipart
        val response = apiService.postMultipart(
            pathUrl = pathUrl,
            headerMap = headersString,
            queryParams = queryParamsString,
            body = jsonRequestBody,
            files = multipartFiles
        )

        return@executeSafe when {
            responseWrappedModel == Nothing::class.java || response.code() == 204 || response.body()?.string().isNullOrBlank() -> Unit as ResponseBody
            response.isSuccessful -> response.body()?.string()?.getModelFromJSON(responseWrappedModel)
                ?: throw IOException("Empty response body")
            else -> {
                // Manually map the error response here instead of throwing HttpException
                val rawErrorBody = try { response.errorBody()?.string() } catch (_: Exception) { null }
                throw NetworkResponseException(
                    statusCode = response.code(),
                    body = rawErrorBody,
                    message = response.message()
                )
            }
        }
    }

    private fun File.asMultipart(partName: String): MultipartBody.Part {
        val requestBody = this.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, this.name, requestBody)
    }

    /**
     * Catches any HttpExceptions thrown automatically by Retrofit (when returning raw bodies)
     * and maps them to our internal boundary exception.
     */
    private inline fun <T> executeSafe(block: () -> T): T {
        return try {
            block()
        } catch (e: HttpException) {
            val rawErrorBody = try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
            throw NetworkResponseException(
                statusCode = e.code(),
                body = rawErrorBody,
                message = e.message(),
                cause = e
            )
        }
    }
}