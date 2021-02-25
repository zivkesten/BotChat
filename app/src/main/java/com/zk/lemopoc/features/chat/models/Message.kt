package com.zk.lemopoc.features.chat.models

open class Message(
    val textInput: String? = null,
    val selection: Boolean? = null,
    val isUserMessage: Boolean
) {
    override fun toString(): String {
        return "$textInput : isUserMessage: $isUserMessage"
    }
}