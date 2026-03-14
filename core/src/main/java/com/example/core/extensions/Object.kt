package com.example.core.extensions

import com.google.gson.Gson
import org.json.JSONObject
import java.lang.reflect.Type

internal fun <M> M.toJson(): String = Gson().toJson(this)

internal fun <M> String.getModelFromJSON(tokenType: Type): M = Gson().fromJson(this, tokenType)

internal fun <M> JSONObject.getModelFromJSON(tokenType: Type): M =
    this.toString().getModelFromJSON(tokenType)

internal fun <M> String.getListOfModelFromJSON(tokenType: Type): ArrayList<M> =
    Gson().fromJson(this, tokenType)