package com.zk.lemopoc.features.chat.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.zk.lemopoc.Message
import com.zk.lemopoc.databinding.ActivityMainBinding
import com.zk.lemopoc.features.chat.ChatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

sealed class Event {
    object StartConversation: Event()
    data class MessageSent(val message: Message): Event()
}

sealed class InputType {
    object TEXT: InputType()
    object NUMBER: InputType()
    data class SELECTION(val trueBtnText: String, val falseBtnText: String): InputType()
    object NONE: InputType()
}

data class UiState(
    val messagesData: List<Message>? = null,
    val inputType: InputType
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Lazy Inject ViewModel
    private val viewModel by viewModel<ChatViewModel>()

    private val chatAdapter: MessageListAdapter = MessageListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBinding()
        viewModel.state.observe(this, Observer {
            Log.v("Zivi", "state.observe: ${it.messagesData}")
            render(it)
        })
    }

    private fun render(state: UiState?) {
        state?.let { state ->
            Log.d("Zivi", "render: ${state.messagesData}")
            Log.d("Zivi", "render inputType: ${state.inputType}")
            state.messagesData?.let { messages ->
                chatAdapter.submitList(messages.toMutableList())
                binding.recyclerChat.smoothScrollToPosition(messages.size.minus(1))
            }
            toggleInputLayout(state.inputType)
            when (state.inputType) {
                is InputType.TEXT -> binding.inputMessage.inputType = android.text.InputType.TYPE_CLASS_TEXT
                is InputType.NUMBER -> binding.inputMessage.inputType = android.text.InputType.TYPE_CLASS_NUMBER
                is InputType.SELECTION -> {
                    binding.yesButton.text = state.inputType.trueBtnText
                    binding.noButton.text = state.inputType.falseBtnText
                    this.binding.root.hideKeyboard()
                }
            }
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

    private fun setupBinding() {
        binding.recyclerChat.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
            viewModel.onEvent(Event.StartConversation)
        }
        binding.buttonSend.setOnClickListener {
            val userMessage = binding.inputMessage.text.toString()
            viewModel.onEvent(Event.MessageSent(Message(userMessage, isUserMessage = true)))
            binding.inputMessage.text.clear()
        }
        binding.yesButton.setOnClickListener {
            sendSelectionMessage(binding.yesButton.text.toString(), true)
        }
        binding.noButton.setOnClickListener {
            sendSelectionMessage(binding.noButton.text.toString(), false)
        }
    }

    private fun sendSelectionMessage(message: String, selection: Boolean) {
        viewModel.onEvent(Event.MessageSent(Message(message, selection = selection, isUserMessage = true)))
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

