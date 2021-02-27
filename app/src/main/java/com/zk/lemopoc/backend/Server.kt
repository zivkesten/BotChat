package com.zk.lemopoc.backend

import com.zk.lemopoc.backend.models.ResponseContent
import com.zk.lemopoc.backend.models.ServerResponse
import com.zk.lemopoc.createJsonPayload
import com.zk.lemopoc.features.chat.models.Message
import com.zk.lemopoc.features.chat.models.MessageType
import com.zk.lemopoc.features.chat.viewModel.parseServerRequest
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
                    "Nice to meet you ${prevMsg.textInput} :) ",
                    messageType = MessageType.Bot)
                )
                sendMessage(
                    Message(
                        "What is your phone number?",
                        messageType = MessageType.Bot),
                    shouldReply = true
                )
            }
            Step.THREE.value -> {
                sendMessage(Message(messageType = MessageType.BotTyping))
                sendMessage(Message(
                    "Do you agree to our terms of service?",
                    messageType = MessageType.Bot),
                    shouldReply = true
                )
            }
            Step.FOUR.value -> {
                sendMessage(
                    Message("Thanks!", messageType = MessageType.Bot))
                sendMessage(
                    Message(messageType = MessageType.BotTyping))
                sendMessage(
                    Message("This is the last step!",
                    messageType = MessageType.Bot)
                )
                sendMessage(
                    Message(messageType = MessageType.BotTyping))
                sendMessage(
                    Message("What do you want to do now?",
                    messageType = MessageType.Bot),
                    shouldReply = true
                )
            }
            Step.FIVE.value -> {
                sendMessage(
                    Message(
                    "Bye Bye!!", messageType = MessageType.Bot)
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
            "What is your name?",
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
        return Message("Hello, I am Ziv!", messageType = MessageType.Bot)
    }
}
