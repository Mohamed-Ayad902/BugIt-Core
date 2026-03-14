package com.example.core.strategies.image

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class ImgBBDto(
    @SerializedName("data") val data: ImgBBData,
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int
)

@Keep
internal data class ImgBBData(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("url_viewer") val urlViewer: String,
    @SerializedName("url") val url: String,
    @SerializedName("display_url") val displayUrl: String,
    @SerializedName("width") val width: String,
    @SerializedName("height") val height: String,
    @SerializedName("size") val size: String,
    @SerializedName("time") val time: String,
    @SerializedName("expiration") val expiration: String,
    @SerializedName("image") val image: ImgBBImageDetails,
    @SerializedName("thumb") val thumb: ImgBBImageDetails,
    @SerializedName("medium") val medium: ImgBBImageDetails?,
    @SerializedName("delete_url") val deleteUrl: String
)

@Keep
internal data class ImgBBImageDetails(
    @SerializedName("filename") val filename: String,
    @SerializedName("name") val name: String,
    @SerializedName("mime") val mime: String,
    @SerializedName("extension") val extension: String,
    @SerializedName("url") val url: String
)