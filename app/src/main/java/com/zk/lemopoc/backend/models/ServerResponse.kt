package com.zk.lemopoc.backend.models

import com.zk.lemopoc.features.chat.models.Message
import com.zk.lemopoc.backend.Step

data class ResponseContent(
    val messageContent: Message? = null,
)

data class ServerResponse(
    val content: ResponseContent,
    val currentSep: Step,
    val shouldReply: Boolean = false
)