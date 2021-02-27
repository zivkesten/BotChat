package com.zk.lemopoc

import android.content.Context
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson

fun createJsonPayload(message: Any): String {
    return Gson().toJson(message)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Editable.isLetters(): Boolean {
    return this.toString().filter { it in 'a'..'z' }.length == this.toString().length
}
