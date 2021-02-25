package com.zk.lemopoc.features.chat.repository

import com.google.gson.Gson
import com.zk.lemopoc.backend.Server
import com.zk.lemopoc.backend.Step
import com.zk.lemopoc.backend.models.ServerRequest
import com.zk.lemopoc.backend.models.ServerResponse
import com.zk.lemopoc.createJsonPayload
import com.zk.lemopoc.features.chat.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.dsl.module

val repositoryModule = module {
    single<ChatRepository> { ChatRepositoryImpl(get()) }
}

interface ChatRepository {
    val answers: Flow<Answer>
    suspend fun startConversation()
    suspend fun message(message: Message)
}

sealed class Content {
    data class MessageContent(val message: Message): Content()
    object Typing: Content()
    object Restart: Content()

}
data class Answer(
    val content: Content,
    val currentStep: Step,
)

class ChatRepositoryImpl(private val server: Server): ChatRepository {
    override val answers: Flow<Answer> = server.answers
        .map { json -> mapJsonToResponse(json) }
        .map { response -> mapResponseToAnswer(response) }

    private fun mapResponseToAnswer(
        response: ServerResponse
    ): Answer {
        val content = mapResponseContentToAnswerContent(response)
        return Answer(
            content,
            response.currentSep,
        )
    }

    private fun mapResponseContentToAnswerContent(
        response: ServerResponse
    ): Content {
        response.content.messageContent?.let {
            return Content.MessageContent(it)
        }
        return Content.Restart
    }

    private fun mapJsonToResponse(
        jsonPayload: String
    ): ServerResponse {
        return Gson().fromJson(
            jsonPayload,
            ServerResponse::class.java
        )
    }

    override suspend fun startConversation() {
        server.startConversation()
    }

    override suspend fun message(message: Message) {
        server.message(createJsonPayload(ServerRequest(message)))
    }
}