package com.zk.lemopoc.features.chat.ui.view

import android.content.res.Resources
import android.graphics.PorterDuff
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.zk.lemopoc.*
import com.zk.lemopoc.databinding.ActivityMainBinding
import com.zk.lemopoc.features.chat.models.Message
import com.zk.lemopoc.features.chat.models.MessageType
import com.zk.lemopoc.features.chat.ui.list.MessageListAdapter
import com.zk.lemopoc.features.chat.viewModel.ChatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

sealed class Event {
    object StartConversation: Event()
    data class UserTyping(val typing: Boolean): Event()
    data class MessageSent(val message: Message): Event()
}

sealed class InputType {
    object TEXT: InputType()
    object NUMBER: InputType()
    data class SELECTION(val trueBtnTextRes: Int, val falseBtnTextRes: Int): InputType()
    object NONE: InputType()
}

data class UiState(
    val messagesData: List<Message>? = null,
    val inputType: InputType,
    val enableReply: Boolean = false
)

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModel<ChatViewModel>()

    private val chatAdapter: MessageListAdapter = MessageListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        viewModel.state.observe(this, {
            render(it)
        })
    }

    private fun setupViews() {
        setupChatList()
        setupSendButton()
        setupInput()
        setupSelectionButtons()
    }

    private fun setupChatList() {
        binding.recyclerChat.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
            viewModel.onEvent(Event.StartConversation)
        }
    }

    private fun setupInput() {
        binding.inputMessage.onTextChanged { text ->
            // when text is empty remove any error messages
            if(text.isEmpty()) toggleInputError(View.INVISIBLE)
            // Set a "typing" message when user is currently typing
            viewModel.onEvent(Event.UserTyping(text.isNotEmpty()))
            when (viewModel.state.value?.inputType) {
                is InputType.TEXT -> {
                    binding.buttonSend.isEnabled = text.isLetters()
                    if (!text.isLetters()) {
                        toggleInputError(View.VISIBLE, getString(R.string.letters_only_warning))
                    } else {
                        toggleInputError(View.INVISIBLE)
                    }
                }
                is InputType.NUMBER -> {
                    binding.buttonSend.isEnabled = text.isDigitsOnly()
                    if (!text.isDigitsOnly()) {
                        toggleInputError(View.VISIBLE, getString(R.string.numbers_only_warning))
                    } else {
                        toggleInputError(View.INVISIBLE)
                    }
                }

                else -> Log.d(TAG, "No action required on selection input state")
            }

        }
    }

    private fun toggleInputError(visibility: Int, text: String? = null) {
        binding.inputMessageError.visibility = visibility
        text?. let {
            binding.inputMessageError.text = it
        }
    }

    private fun setupSelectionButtons() {
        binding.yesButton.setOnClickListener {
            sendSelectionMessage(binding.yesButton.text.toString(), true)
            binding.yesButton.isEnabled = false
        }

        binding.noButton.setOnClickListener {
            sendSelectionMessage(binding.noButton.text.toString(), false)
            binding.noButton.isEnabled = false
        }
    }

    private fun sendSelectionMessage(message: String, selection: Boolean) {
        sendMessageEvent(Message(
            message,
            selection = selection,
            messageType = MessageType.User)
        )
    }

    private fun sendMessageEvent(message: Message) {
        viewModel.onEvent(Event.MessageSent(message))
    }

    private fun setupSendButton() {
        binding.buttonSend.setColorFilter(resources.getColor(R.color.pink), PorterDuff.Mode.SRC_IN)
        if (binding.inputMessage.text.isEmpty()) {
            binding.buttonSend.isEnabled = false
        }
        binding.buttonSend.setDebounceClickListener {
            val userMessage = binding.inputMessage.text.toString()
            val message = Message(userMessage, messageType = MessageType.User)
            sendMessageEvent(message)
            binding.inputMessage.text.clear()
        }
    }

    private fun render(state: UiState?) {
        state?.let { uiState ->
            uiState.messagesData?.let { messages ->
                chatAdapter.submitList(messages.toMutableList())
                // On smaller screens the list could be hidden by the input, so we
                // Scroll the list to its bottom
                binding.recyclerChat.smoothScrollToPosition(messages.size.minus(1))
            }

            // Toggle input type text|number|selection
            renderInputType(uiState)

            // Enable option to reply when possible
            with(uiState.enableReply) {
                binding.inputMessage.isEnabled = this
                binding.buttonSend.isEnabled = this
                binding.yesButton.isEnabled = this
                binding.noButton.isEnabled = this
            }
        }
    }

    private fun renderInputType(uiState: UiState) {
        toggleInputLayout(uiState.inputType)
        when (uiState.inputType) {
            is InputType.TEXT -> {
                binding.inputMessage.inputType = android.text.InputType.TYPE_CLASS_TEXT
            }
            is InputType.NUMBER -> {
                binding.inputMessage.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            }
            is InputType.SELECTION -> {
                binding.yesButton.text = getString(uiState.inputType.trueBtnTextRes)
                binding.noButton.text = getString(uiState.inputType.falseBtnTextRes)
                this.binding.root.hideKeyboard()
            }
            is InputType.NONE -> this.binding.root.hideKeyboard()
        }
    }

    private fun toggleInputLayout(inputType: InputType) {
        when (inputType) {
            is InputType.TEXT, InputType.NUMBER, InputType.NONE -> {
                binding.inputMessageLayout.visibility = View.VISIBLE
                binding.selectionLayout.visibility = View.GONE
            }
            is InputType.SELECTION -> {
                binding.inputMessageLayout.visibility = View.GONE
                binding.selectionLayout.visibility = View.VISIBLE
            }
        }
    }
}

