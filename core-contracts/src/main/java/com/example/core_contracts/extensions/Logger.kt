package com.example.core_contracts.extensions

import android.util.Log
import com.example.core_contracts.BuildConfig

fun Any?.loge(tag: String = "BugItLog") {
    if (BuildConfig.DEBUG) Log.e(tag, ">> $this")
}

fun Any?.logw(tag: String = "BugItLog") {
    if (BuildConfig.DEBUG) Log.w(tag, ">> $this")
}

fun Any?.logd(tag: String = "BugItLog") {
    if (BuildConfig.DEBUG) Log.d(tag, ">> $this")
}