package com.zk.lemopoc

import com.google.gson.Gson

fun createJsonPayload(message: Any): String {
    return Gson().toJson(message)
}