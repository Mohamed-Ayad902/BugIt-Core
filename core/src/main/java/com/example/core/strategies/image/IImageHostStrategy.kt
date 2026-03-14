package com.example.core.strategies.image

interface IImageHostStrategy {
    suspend fun uploadImage(imageBytes: ByteArray, expirationSeconds: Int): String // returns image url
}