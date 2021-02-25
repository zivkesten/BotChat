package com.zk.lemopoc

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(private val server: Server): ViewModel() {

    private val chatList = mutableListOf<Message>()

    private val _state = MutableLiveData<UiState>()

    val state: LiveData<UiState> get() = _state

    init {
        viewModelScope.launch {
            Log.i("Zivi", "collecting in VM")
            server.answers.collect { answers ->
                Log.e("Zivi", "answers: $answers")
                chatList.add(stringify(answers))
                _state.value = UiState(messagesData = chatList)
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.MessageSent -> sendToServer(event)
            is Event.StartConversation -> startConversation()
        }
    }

    private fun startConversation() {
        viewModelScope.launch(Dispatchers.IO) {
            server.startConversation()
        }
    }

    private fun sendToServer(event: Event.MessageSent) {
        viewModelScope.launch(Dispatchers.IO) {
            server.message(jsonify(event.message))
        }
    }
}

fun jsonify(message: Message): String {
    return Gson().toJson(message)
}

fun stringify(message: String): Message {
    return Gson().fromJson(message, Message::class.java)
}