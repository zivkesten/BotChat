package com.zk.lemopoc.backend.models

import com.zk.lemopoc.features.chat.models.Message

data class ServerRequest(
    val message: Message
)

