package com.zk.lemopoc.features.chat.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.zk.lemopoc.databinding.*
import com.zk.lemopoc.features.chat.models.Message
import com.zk.lemopoc.features.chat.models.MessageType
import com.zk.lemopoc.features.chat.ui.list.listItems.*


enum class MessageTypes(val value: Int) {
    USER(1),
    USER_TYPING(2),
    BOT(3),
    BOT_TYPING(4),
    SEPARATOR(5);
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
            MessageType.UserTyping.value -> {
                val binding = ChatBubbleUserTypingBinding
                    .inflate(inflater, parent, false)
                UserTypingMessageViewHolder(binding)
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
            MessageType.UserTyping -> MessageTypes.USER_TYPING.value
            MessageType.Bot -> MessageTypes.BOT.value
            MessageType.Separator -> MessageTypes.SEPARATOR.value
            MessageType.BotTyping -> MessageTypes.BOT_TYPING.value
        }
    }
}