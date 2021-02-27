package com.zk.lemopoc.features.chat.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zk.lemopoc.R
import com.zk.lemopoc.backend.Step
import com.zk.lemopoc.features.chat.models.Message
import com.zk.lemopoc.features.chat.models.MessageType
import com.zk.lemopoc.features.chat.repository.ChatRepository
import com.zk.lemopoc.features.chat.repository.Content
import com.zk.lemopoc.features.chat.ui.view.Event
import com.zk.lemopoc.features.chat.ui.view.InputType
import com.zk.lemopoc.features.chat.ui.view.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

data class Answer(
    val content: Content,
    val currentStep: Step,
    val shouldReply: Boolean = false
)

class ChatViewModel(private val repository: ChatRepository): ViewModel() {

    // Using a stack
    private val messageList = Stack<Message>()

    private val _state = MutableLiveData<UiState>()

    val state: LiveData<UiState> get() = _state

    init {
        viewModelScope.launch {

            repository.answers.collect { answer ->
                when (answer.content) {
                    is Content.MessageContent -> {
                        postMessage(
                            answer.content.message,
                            answer.currentStep,
                            answer.shouldReply
                        )
                    }
                    is Content.Restart -> {
                        postMessage(Message(
                            messageType = MessageType.Separator),
                            answer.currentStep,
                            answer.shouldReply
                        )

                    }
                    is Content.Typing -> {
                        postMessage(Message(
                            messageType = MessageType.BotTyping),
                            answer.currentStep,
                            answer.shouldReply
                        )
                    }
                }
            }
        }
    }

    private fun postMessage(message: Message, step: Step, enableReply: Boolean) {

        if (lastMessage()?.messageType == MessageType.BotTyping
            || lastMessage()?.messageType == MessageType.UserTyping) {
            messageList.pop()
        }
        messageList.push(message)
        _state.value = UiState(
            messagesData = messageList,
            inputTypeByStep(step),
            enableReply
        )
    }

    private fun inputTypeByStep(currentStep: Step): InputType {
        return when (currentStep) {
            Step.ONE -> InputType.TEXT
            Step.TWO -> InputType.NUMBER
            Step.THREE -> InputType.SELECTION(R.string.yes, R.string.no)
            Step.FOUR -> InputType.SELECTION(R.string.restart, R.string.exit)
            Step.FIVE -> InputType.NONE
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.MessageSent -> {
                removeTypingMessageIfNeeded()
                sendToServer(event)
                event.message.textInput?.let {
                    messageList.push(event.message)
                    postState()
                }
            }
            is Event.StartConversation -> startConversation()
            is Event.UserTyping -> {
                if (!event.typing) {
                    removeTypingMessageIfNeeded()
                    return
                }
                if (lastMessage()?.messageType != MessageType.UserTyping) {
                    messageList.push(Message(messageType = MessageType.UserTyping))
                    postState()
                }
            }
        }
    }

    private fun lastMessage(): Message? {
        // peek at the last message to see if its a typing message, if stack is empty return null
        return try {
            messageList.peek()
        } catch (e: EmptyStackException) {
            null
        }
    }

    private fun postState() {
        _state.value = _state.value?.copy(
            messagesData = messageList
        )
    }

    private fun removeTypingMessageIfNeeded() {
        val lastMessageType = lastMessage()?.messageType
        if (lastMessageType == MessageType.UserTyping) {
            messageList.pop()
        }
        postState()
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