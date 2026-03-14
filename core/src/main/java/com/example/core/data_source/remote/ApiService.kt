package com.example.core.data_source.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface ApiService {

    @DELETE("{pathUrl}")
    @JvmSuppressWildcards
    suspend fun delete(
        @Path(value = "pathUrl", encoded = true) pathUrl: String, @Body body: Any,
        @HeaderMap headerMap: Map<String, Any>, @QueryMap queryParams: Map<String, Any>
    ): ResponseBody

    @POST("{pathUrl}")
    @JvmSuppressWildcards
    suspend fun post(
        @Path(value = "pathUrl", encoded = true) pathUrl: String, @Body body: Any,
        @HeaderMap headerMap: Map<String, Any>, @QueryMap queryParams: Map<String, Any>
    ): ResponseBody

    @GET("{pathUrl}")
    @JvmSuppressWildcards
    suspend fun get(
        @Path(value = "pathUrl", encoded = true) pathUrl: String,
        @HeaderMap headerMap: Map<String, Any>, @QueryMap queryParams: Map<String, Any>
    ): ResponseBody

    @Multipart
    @POST("{pathUrl}")
    @JvmSuppressWildcards
    suspend fun postWithImagesFile(
        @Path(value = "pathUrl", encoded = true) pathUrl: String,
        @Part files: List<MultipartBody.Part>,
        @Part("data") requestBody: Any,
        @HeaderMap headerMap: Map<String, Any>,
        @QueryMap queryParams: Map<String, Any>
    ): Response<ResponseBody>

    @Multipart
    @POST
    suspend fun postMultipart(
        @Url pathUrl: String,
        @HeaderMap headerMap: Map<String, String>,
        @QueryMap queryParams: Map<String, String>,
        @Part("body") body: RequestBody?,
        @Part files: List<MultipartBody.Part>
    ): Response<ResponseBody>

}