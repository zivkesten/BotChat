package com.zk.lemopoc.backend

import com.zk.lemopoc.backend.models.ServerResponse
import com.zk.lemopoc.features.chat.models.Message
import com.zk.lemopoc.features.chat.viewModel.createJsonPayload
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

enum class Steps(val value: Int) {
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

    private var currentStep: Steps = Steps.ONE

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

    private suspend fun botMessageByStep(step: Steps, prevMsg: Message) {
        when (step.value) {
            Steps.ONE.value -> {
                firstStepMessages()
            }
            Steps.TWO.value -> {
                delay(1000)
                val msg1 =  Message("Nice to meet you ${prevMsg.textInput} :) ", isUserMessage = false)
                val msg2 =  Message("What is your phone number?", isUserMessage = false)
                _answers.emit(createJsonPayload(ServerResponse(msg1, currentStep)))
                delay(1000)
                _answers.emit(createJsonPayload(ServerResponse(msg2, currentStep)))
            }
            Steps.THREE.value -> {
                delay(2000)
                val msg = Message("Do you agree to our terms of service?", isUserMessage = false)
                _answers.emit(createJsonPayload(ServerResponse(msg, currentStep)))
            }
            Steps.FOUR.value -> {
                val msg1 =  Message("Thanks!", isUserMessage = false)
                val msg2 =  Message("This is the last step!", isUserMessage = false)
                val msg3 =  Message("What do you want to do now?", isUserMessage = false)
                _answers.emit(createJsonPayload(ServerResponse(msg1, currentStep)))
                delay(2000)
                _answers.emit(createJsonPayload(ServerResponse(msg2, currentStep)))
                delay(2000)
                _answers.emit(createJsonPayload(ServerResponse(msg3, currentStep)))
            }
            Steps.FIVE.value -> {
                delay(2000)
                val msg1 =  Message("Bye Bye!!", isUserMessage = false)
                _answers.emit(createJsonPayload(ServerResponse(msg1, currentStep)))
            }
        }

    }

    private suspend fun firstStepMessages() {
        val msg1 = defaultMessage()
        val msg2 = Message("What is your name?", isUserMessage = false)
        _answers.emit(createJsonPayload(ServerResponse(msg1, currentStep)))
        delay(1000)
        _answers.emit(createJsonPayload(ServerResponse(msg2, currentStep)))
    }

    private fun setNextStep(message: Message?) {
        currentStep = when (currentStep) {
            Steps.ONE -> Steps.TWO
            Steps.TWO -> Steps.THREE
            Steps.THREE -> {
                if (message?.selection == true) {
                    Steps.FOUR
                } else {
                    Steps.FIVE
                }
            }
            Steps.FOUR -> {
                if (message?.selection == true) {
                    Steps.ONE
                } else {
                    Steps.FIVE
                }
            }
            Steps.FIVE -> Steps.ONE
        }
    }

    private fun defaultMessage(): Message {
        return Message("Hello, I am Ziv!", isUserMessage = false)
    }

}
