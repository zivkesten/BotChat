package com.zk.lemopoc.features.chat.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.zk.lemopoc.R
import com.zk.lemopoc.backend.Steps
import com.zk.lemopoc.backend.models.ServerRequest
import com.zk.lemopoc.backend.models.ServerResponse
import com.zk.lemopoc.features.chat.models.Message
import com.zk.lemopoc.features.chat.repository.Answer
import com.zk.lemopoc.features.chat.repository.ChatRepository
import com.zk.lemopoc.features.chat.ui.view.Event
import com.zk.lemopoc.features.chat.ui.view.InputType
import com.zk.lemopoc.features.chat.ui.view.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository): ViewModel() {

    private val chatList = mutableListOf<Message>()

    private val _state = MutableLiveData<UiState>()

    val state: LiveData<UiState> get() = _state

    init {
        viewModelScope.launch {
            repository.answers.collect { answer ->
                chatList.add(answer.message)
                _state.value = UiState(messagesData = chatList, inputTypeByStep(answer))
            }
        }
    }

    private fun inputTypeByStep(answer: Answer): InputType {
        return when (answer.currentStep) {
            Steps.ONE -> InputType.TEXT
            Steps.TWO -> InputType.NUMBER
            Steps.THREE -> InputType.SELECTION(R.string.yes, R.string.no)
            Steps.FOUR -> InputType.SELECTION(R.string.restart, R.string.exit)
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