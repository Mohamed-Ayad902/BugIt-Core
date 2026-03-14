package com.example.core_contracts.utils

import com.example.core_contracts.exceptions.BugItExceptions

sealed class Resource<out Model> {

    data class Progress<Model>(val loading: Boolean, val partialData: Model? = null) :
        Resource<Model>()

    data class Success<out Model>(val model: Model) : Resource<Model>()

    data class Failure(val exception: BugItExceptions) : Resource<Nothing>()

    companion object {
        fun <Model> loading(
            loading: Boolean = true, partialData: Model? = null
        ): Resource<Model> = Progress(loading, partialData)

        fun <Model> success(model: Model): Resource<Model> = Success(model)

        fun <Model> failure(exception: BugItExceptions): Resource<Model> = Failure(exception)
    }
}