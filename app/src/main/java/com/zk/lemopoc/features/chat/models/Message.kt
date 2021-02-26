package com.zk.lemopoc.features.chat.models

enum class MessageType(val value: Int) {
    User(1),
    UserTyping(2),
    Bot(3),
    BotTyping(4),
    Separator(5)
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