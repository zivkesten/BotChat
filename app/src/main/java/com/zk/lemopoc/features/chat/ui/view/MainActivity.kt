package com.zk.lemopoc.features.chat.ui.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.zk.lemopoc.databinding.ActivityMainBinding
import com.zk.lemopoc.features.chat.models.Message
import com.zk.lemopoc.features.chat.models.MessageType
import com.zk.lemopoc.features.chat.ui.list.MessageListAdapter
import com.zk.lemopoc.features.chat.viewModel.ChatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

sealed class Event {
    object StartConversation: Event()
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
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModel<ChatViewModel>()

    private val chatAdapter: MessageListAdapter = MessageListAdapter()

    // These text watchers are meant for validating the input in different steps
    private val textInputTextWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(text: Editable?) {
            text?.let {
                binding.buttonSend.isEnabled = it.isLetters()
            }
        }
    }

    private val numbersInputTextWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(text: Editable?) {
            text?.let {
                binding.buttonSend.isEnabled = it.isDigitsOnly()
            }
        }
    }

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
        binding.inputMessage.addTextChangedListener {
            binding.buttonSend.isEnabled = !it.isNullOrEmpty()
        }
    }

    private fun setupSelectionButtons() {
        binding.yesButton.setOnClickListener {
            sendSelectionMessage(binding.yesButton.text.toString(), true)
        }
        binding.noButton.setOnClickListener {
            sendSelectionMessage(binding.noButton.text.toString(), false)
        }
    }

    private fun sendSelectionMessage(message: String, selection: Boolean) {
        viewModel.onEvent(
            Event.MessageSent(
                Message(
                    message,
                    selection = selection,
                    messageType = MessageType.User
                )
            )
        )
    }

    private fun setupSendButton() {
        if (binding.inputMessage.text.isEmpty()) {
            binding.buttonSend.isEnabled = false
        }
        binding.buttonSend.setOnClickListener {
            val userMessage = binding.inputMessage.text.toString()
            val message = Message(userMessage, messageType = MessageType.User)
            viewModel.onEvent(Event.MessageSent(message))
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
        }
    }

    private fun renderInputType(uiState: UiState) {
        toggleInputLayout(uiState.inputType)
        when (uiState.inputType) {
            is InputType.TEXT -> {
                binding.inputMessage.removeTextChangedListener(numbersInputTextWatcher)
                binding.inputMessage.inputType = android.text.InputType.TYPE_CLASS_TEXT
                binding.inputMessage.addTextChangedListener(textInputTextWatcher)
            }
            is InputType.NUMBER -> {
                binding.inputMessage.removeTextChangedListener(textInputTextWatcher)
                binding.inputMessage.inputType = android.text.InputType.TYPE_CLASS_NUMBER
                binding.inputMessage.addTextChangedListener(numbersInputTextWatcher)

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
            is InputType.TEXT, InputType.NUMBER -> {
                binding.inputMessageLayout.visibility = View.VISIBLE
                binding.selectionLayout.visibility = View.GONE
            }
            is InputType.SELECTION -> {
                binding.inputMessageLayout.visibility = View.GONE
                binding.selectionLayout.visibility = View.VISIBLE
            }
            is InputType.NONE -> {
                binding.inputMessageLayout.visibility = View.GONE
                binding.selectionLayout.visibility = View.GONE
            }
        }
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Editable.isLetters(): Boolean {
    return this.toString().filter { it in 'a'..'z' }.length == this.toString().length
}


