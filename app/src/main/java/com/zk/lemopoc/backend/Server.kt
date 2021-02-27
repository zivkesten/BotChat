package com.zk.lemopoc.backend

import com.google.gson.Gson
import com.zk.lemopoc.backend.models.ResponseContent
import com.zk.lemopoc.backend.models.ServerRequest
import com.zk.lemopoc.backend.models.ServerResponse
import com.zk.lemopoc.createJsonPayload
import com.zk.lemopoc.features.chat.models.Message
import com.zk.lemopoc.features.chat.models.MessageType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.dsl.module

val serverModule = module {
    single<Server> { ServerImpl() }
}

typealias JsonPayload = String

enum class Step(val value: Int) {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);
}

interface Server {
    val answers: SharedFlow<JsonPayload>
    suspend fun startConversation()
    suspend fun message(message: JsonPayload)
}

class ServerImpl: Server {

    private val mockIODelayTime: Long = 2000

    private var currentStep: Step = Step.ONE

    private val _answers = MutableSharedFlow<JsonPayload>()

    override val answers = _answers.asSharedFlow()

    override suspend fun startConversation() {
        firstStepMessages()
    }

    override suspend fun message(message: JsonPayload) {
        val request = parseServerRequest(message)
        setNextStep(request.message)
        botMessageByStep(currentStep, prevMsg = request.message)
    }

    private suspend fun botMessageByStep(step: Step, prevMsg: Message) {
        when (step.value) {
            Step.ONE.value -> {
                delay(mockIODelayTime)
                _answers.emit(createJsonPayload(ServerResponse(ResponseContent(), currentStep)))
                firstStepMessages()
            }
            Step.TWO.value -> {

                sendMessage(Message(messageType = MessageType.BotTyping))
                sendMessage(Message(
                    ServerConstants.message3 + "${prevMsg.textInput} :)",
                    messageType = MessageType.Bot)
                )
                sendMessage(
                    Message(
                        ServerConstants.message4,
                        messageType = MessageType.Bot),
                    shouldReply = true
                )
            }
            Step.THREE.value -> {
                sendMessage(Message(messageType = MessageType.BotTyping))
                sendMessage(Message(
                    ServerConstants.message5,
                    messageType = MessageType.Bot),
                    shouldReply = true
                )
            }
            Step.FOUR.value -> {
                sendMessage(Message(ServerConstants.message6, messageType = MessageType.Bot))
                sendMessage(Message(messageType = MessageType.BotTyping))
                sendMessage(Message(ServerConstants.message7, messageType = MessageType.Bot))
                sendMessage(Message(messageType = MessageType.BotTyping))
                sendMessage(Message(
                    ServerConstants.message8,
                    messageType = MessageType.Bot),
                    shouldReply = true
                )
            }
            Step.FIVE.value -> {
                sendMessage(Message(
                    ServerConstants.message9, messageType = MessageType.Bot)
                )
            }
        }
    }

    private suspend fun sendMessage(msg1: Message, shouldReply: Boolean = false) {
        delay(mockIODelayTime)
        _answers.emit(
            createJsonPayload(
                ServerResponse(
                    ResponseContent(msg1),
                    currentStep,
                    shouldReply
                )
            )
        )
    }

    private suspend fun firstStepMessages() {
        sendMessage(Message(messageType = MessageType.BotTyping))
        sendMessage(defaultMessage())
        sendMessage(Message(messageType = MessageType.BotTyping))
        sendMessage(Message(
            ServerConstants.message2,
            messageType = MessageType.Bot
        ), shouldReply = true)
    }

    private fun setNextStep(message: Message?) {
        currentStep = when (currentStep) {
            Step.ONE -> Step.TWO
            Step.TWO -> Step.THREE
            Step.THREE -> {
                if (message?.selection == true) {
                    Step.FOUR
                } else {
                    Step.FIVE
                }
            }
            Step.FOUR -> {
                if (message?.selection == true) {
                    Step.ONE
                } else {
                    Step.FIVE
                }
            }
            Step.FIVE -> Step.ONE
        }
    }

    private fun defaultMessage(): Message {
        return Message(ServerConstants.message1, messageType = MessageType.Bot)
    }

    private fun parseServerRequest(jsonPayload: String): ServerRequest {
        return Gson().fromJson(jsonPayload, ServerRequest::class.java)
    }
}
