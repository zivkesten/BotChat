package com.zk.lemopoc

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.dsl.module

val serverModule = module {
    single { Server() }
}

sealed class Steps {
    object StepOne: Steps()
    object StepTwo: Steps()
    object StepThree: Steps()
    object StepFour: Steps()
}

class Server {

    private var currentSep: Steps = Steps.StepOne

    private val _answers = MutableSharedFlow<String>()

    val answers = _answers.asSharedFlow()

    suspend fun startConversation() {
        Log.w("Zivi", "startConversation")
        val msg1 =  defaultMessage()
        val msg2 =  Message("write name", isUserMessage = false)
        Log.d("Zivi", "first answer: ${msg1.textInput}")
        _answers.emit(jsonify(msg1))
        delay(2000)
        Log.d("Zivi", "second answer: ${msg2.textInput}")
        _answers.emit(jsonify(msg2))
    }

    suspend fun message(message: String) {
        val message = stringify(message)
        botMessageByStep(currentSep, prevMsg = message)
    }

    private suspend fun botMessageByStep(currentSep: Steps, prevMsg: Message) {
//        when (currentSep) {
//            is Steps.StepOne -> {
//                val msg1 =  defaultMessage()
//                val msg2 =  Message("write name", isUserMessage = false)
//                answers.value = jsonify(msg1)
//                delay(2000)
//                answers.value = jsonify(msg2)
//            }
//            is Steps.StepTwo -> {
//                val msg1 =  Message("nuce to meet ${prevMsg.textInput}", isUserMessage = false)
//                val msg2 =  Message("write numer", isUserMessage = false)
//                answers.value = jsonify(msg1)
//                delay(2000)
//                answers.value = jsonify(msg2)
//
//            }
//            is Steps.StepThree -> answers.value = jsonify(Message("agree?", isUserMessage = false))
//            is Steps.StepFour -> {
//                val msg1 =  Message("thanks", isUserMessage = false)
//                val msg2 =  Message("last", isUserMessage = false)
//                val msg3 =  Message("what next", isUserMessage = false)
//                answers.value = jsonify(msg1)
//                delay(2000)
//                answers.value = jsonify(msg2)
//                delay(2000)
//                answers.value = jsonify(msg3)
//
//            }
//        }
        incrementSteps()
    }

    private fun incrementSteps() {
        currentSep = when (currentSep) {
            is Steps.StepOne -> Steps.StepTwo
            is Steps.StepTwo -> Steps.StepThree
            is Steps.StepThree -> Steps.StepFour
            is Steps.StepFour -> Steps.StepOne
        }
    }

    private fun defaultMessage(): Message {
        return Message("dscxdd", isUserMessage = false)
    }

}
