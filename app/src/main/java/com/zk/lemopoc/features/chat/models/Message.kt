package com.zk.lemopoc.features.chat.models

enum class MessageType(val value: Int) {
    User(1),
    Bot(2),
    BotTyping(3),
    Separator(4)
}

data class Message(
    val textInput: String? = null,
    val selection: Boolean? = null,
    val messageType: MessageType
) {
    override fun toString(): String {
        return "$textInput : messageType: $messageType"
    }
}