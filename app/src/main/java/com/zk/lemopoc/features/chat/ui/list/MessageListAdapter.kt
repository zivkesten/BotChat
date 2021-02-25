package com.zk.lemopoc.features.chat.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.zk.lemopoc.databinding.ChatBubbleBotBinding
import com.zk.lemopoc.databinding.ChatBubbleUserBinding
import com.zk.lemopoc.databinding.SeparatorBubbleBinding
import com.zk.lemopoc.features.chat.models.Message
import com.zk.lemopoc.features.chat.models.MessageType


enum class Types(val value: Int) {
    USER(1),
    BOT(2),
    SEPARATOR(3);
}

class MessageListAdapter :
    ListAdapter<Message, MultiViewViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MultiViewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Types.USER.value -> {
                val binding = ChatBubbleUserBinding
                    .inflate(inflater, parent, false)
                UserMessageViewHolder(binding)
            }
            Types.BOT.value -> {
                val binding = ChatBubbleBotBinding
                    .inflate(inflater, parent, false)
                BotMessageViewHolder(binding)
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
            MessageType.User -> Types.USER.value
            MessageType.Bot -> Types.BOT.value
            MessageType.Separator -> Types.SEPARATOR.value
        }
    }
}