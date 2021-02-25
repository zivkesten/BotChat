package com.zk.lemopoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.idanatz.oneadapter.OneAdapter
import com.zk.lemopoc.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

sealed class Event {
    object StartConversation: Event()
    data class MessageSent(val message: Message): Event()
}

data class UiState(
    val messagesData: List<Message>? = null,
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Lazy Inject ViewModel
    private val viewModel by viewModel<ChatViewModel>()

    private lateinit var chatAdapter: OneAdapter

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
        state?.let {
            Log.d("Zivi", "render: ${it.messagesData}")
            it.messagesData?.let { messages ->
                chatAdapter.setItems(messages)
            }

        }
    }

    private fun setupBinding() {
        binding.recyclerChat.apply {
            layoutManager = LinearLayoutManager(context)
            chatAdapter = OneAdapter(this) {
                itemModules += BotModule()
                itemModules += UserModule()
                //emptinessModule = EmptyListModule()
                }
            }
            viewModel.onEvent(Event.StartConversation)
        }
    }
