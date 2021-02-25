package com.zk.lemopoc.features.chat.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.zk.lemopoc.databinding.ChatBubbleBotBinding
import com.zk.lemopoc.databinding.ChatBubbleBotTypingBinding
import com.zk.lemopoc.databinding.ChatBubbleUserBinding
import com.zk.lemopoc.databinding.SeparatorBubbleBinding
import com.zk.lemopoc.features.chat.models.Message
import com.zk.lemopoc.features.chat.models.MessageType


enum class MessageTypes(val value: Int) {
    USER(1),
    BOT(2),
    BOT_TYPING(3),
    SEPARATOR(4);
}

class MessageListAdapter :
    ListAdapter<Message, MultiViewViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MultiViewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MessageTypes.USER.value -> {
                val binding = ChatBubbleUserBinding.inflate(inflater, parent, false)
                UserMessageViewHolder(binding)
            }
            MessageTypes.BOT.value -> {
                val binding = ChatBubbleBotBinding
                    .inflate(inflater, parent, false)
                BotMessageViewHolder(binding)
            }
            MessageType.BotTyping.value -> {
                val binding = ChatBubbleBotTypingBinding
                    .inflate(inflater, parent, false)
                BotTypingMessageViewHolder(binding)
            }
            // Separator
            else -> {
                val binding = SeparatorBubbleBinding
                    .inflate(inflater, parent, false)
                SeparatorViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(
        holder: MultiViewViewHolder,
        position: Int
    ) {
        holder.onBindVewHolder(position, getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return when (message.messageType) {
            MessageType.User -> MessageTypes.USER.value
            MessageType.Bot -> MessageTypes.BOT.value
            MessageType.Separator -> MessageTypes.SEPARATOR.value
            MessageType.BotTyping -> MessageTypes.BOT_TYPING.value
        }
    }
}