package com.zk.lemopoc.features.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.zk.lemopoc.*
import com.zk.lemopoc.backend.models.ServerRequest
import com.zk.lemopoc.backend.models.ServerResponse
import com.zk.lemopoc.backend.Steps
import com.zk.lemopoc.features.chat.ui.Event
import com.zk.lemopoc.features.chat.ui.InputType
import com.zk.lemopoc.features.chat.ui.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository): ViewModel() {

    private val chatList = mutableListOf<Message>()

    private val _state = MutableLiveData<UiState>()

    val state: LiveData<UiState> get() = _state

    init {
        viewModelScope.launch {
            Log.i("Zivi", "collecting in VM")
            repository.answers.collect { answer ->
                Log.e("Zivi", "answers: $answer")
                chatList.add(answer.message)
                _state.value = UiState(messagesData = chatList, inputTypeByStep(answer))
            }
        }
    }

    private fun inputTypeByStep(answer: Answer): InputType {
        return when (answer.currentStep) {
            Steps.ONE -> InputType.TEXT
            Steps.TWO -> InputType.NUMBER
            Steps.THREE -> InputType.SELECTION("Yes", "No")
            Steps.FOUR -> InputType.SELECTION("RESTART", "EXIT")
            Steps.FIVE -> InputType.NONE
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.MessageSent -> {
               sendToServer(event)
               event.message.textInput?.let {
                   chatList.add(event.message)
                   _state.value = _state.value?.copy(messagesData = chatList)
               }
            }
            is Event.StartConversation -> startConversation()
        }
    }

    private fun startConversation() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.startConversation()
        }
    }

    private fun sendToServer(event: Event.MessageSent) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.message(event.message)
        }
    }
}

fun createJsonPayload(message: Any): String {
    return Gson().toJson(message)
}

fun parseServerRequest(jsonPayload: String): ServerRequest {
    return Gson().fromJson(jsonPayload, ServerRequest::class.java)
}

fun parseServerResponse(jsonPayload: String): ServerResponse {
    return Gson().fromJson(jsonPayload, ServerResponse::class.java)
}