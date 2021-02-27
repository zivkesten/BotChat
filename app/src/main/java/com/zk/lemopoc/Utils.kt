package com.zk.lemopoc

import android.content.Context
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.gson.Gson

fun createJsonPayload(message: Any): String {
    return Gson().toJson(message)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun String.isLetters(): Boolean {
    return this.filter { it in 'a'..'z' }.length == this.length
}

fun EditText.onTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(editable: Editable?) {}

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, after: Int) {
            afterTextChanged.invoke(text.toString())
        }
    })
}

fun View.setDebounceClickListener(action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < 2000L) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}