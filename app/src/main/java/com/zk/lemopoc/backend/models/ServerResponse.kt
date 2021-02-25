package com.zk.lemopoc.backend.models

import com.zk.lemopoc.Message
import com.zk.lemopoc.backend.Steps

data class ServerResponse(
    val message: Message,
    val currentSep: Steps
)