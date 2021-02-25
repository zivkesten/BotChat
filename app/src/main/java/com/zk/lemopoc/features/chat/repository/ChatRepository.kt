package com.zk.lemopoc.features.chat.repository

import com.zk.lemopoc.features.chat.models.Message
import com.zk.lemopoc.backend.Server
import com.zk.lemopoc.backend.models.ServerRequest
import com.zk.lemopoc.backend.models.ServerResponse
import com.zk.lemopoc.backend.Step
import com.zk.lemopoc.features.chat.viewModel.createJsonPayload
import com.zk.lemopoc.features.chat.viewModel.parseServerResponse
import kotlinx.coroutines.flow.*
import org.koin.dsl.module

val repositoryModule = module {
    single<ChatRepository> { ChatRepositoryImpl(get()) }
}

interface ChatRepository {
    val answers: Flow<Answer>
    suspend fun startConversation()
    suspend fun message(message: Message)
}
data class Answer(
    val message: Message,
    val currentStep: Step,
    val restart: Boolean = false
)

class ChatRepositoryImpl(private val server: Server): ChatRepository {
    override val answers: Flow<Answer> = server.answers
        .map { json -> parseServerResponse(json) }
        .map { response -> mapResponseToAnswer(response) }

    private fun mapResponseToAnswer(response: ServerResponse): Answer {
        return Answer(response.message, response.currentSep, response.restart)
    }

    override suspend fun startConversation() {
        server.startConversation()
    }

    override suspend fun message(message: Message) {
        server.message(createJsonPayload(ServerRequest(message)))
    }
}