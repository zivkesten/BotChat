package com.zk.lemopoc.backend

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
                val msg1 =  Message(messageType = MessageType.Bot)
                _answers.emit(createJsonPayload(ServerResponse(msg1, currentStep, true)))
                delay(2000)
                firstStepMessages()
            }
            Step.TWO.value -> {
                delay(2000)
                val msg1 =  Message("Nice to meet you ${prevMsg.textInput} :) ", messageType = MessageType.Bot)
                val msg2 =  Message("What is your phone number?", messageType = MessageType.Bot)
                _answers.emit(createJsonPayload(ServerResponse(msg1, currentStep)))
                delay(2000)
                _answers.emit(createJsonPayload(ServerResponse(msg2, currentStep)))
            }
            Step.THREE.value -> {
                delay(2000)
                val msg = Message("Do you agree to our terms of service?", messageType = MessageType.Bot)
                _answers.emit(createJsonPayload(ServerResponse(msg, currentStep)))
            }
            Step.FOUR.value -> {
                delay(2000)
                val msg1 =  Message("Thanks!", messageType = MessageType.Bot)
                val msg2 =  Message("This is the last step!", messageType = MessageType.Bot)
                val msg3 =  Message("What do you want to do now?", messageType = MessageType.Bot)
                _answers.emit(createJsonPayload(ServerResponse(msg1, currentStep)))
                delay(2000)
                _answers.emit(createJsonPayload(ServerResponse(msg2, currentStep)))
                delay(2000)
                _answers.emit(createJsonPayload(ServerResponse(msg3, currentStep)))
            }
            Step.FIVE.value -> {
                delay(2000)
                val msg1 =  Message("Bye Bye!!", messageType = MessageType.Bot)
                _answers.emit(createJsonPayload(ServerResponse(msg1, currentStep)))
            }
        }
    }

    private suspend fun firstStepMessages() {
        val msg1 = defaultMessage()
        val msg2 = Message("What is your name?", messageType = MessageType.Bot)
        _answers.emit(createJsonPayload(ServerResponse(msg1, currentStep)))
        delay(2000)
        _answers.emit(createJsonPayload(ServerResponse(msg2, currentStep)))
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
