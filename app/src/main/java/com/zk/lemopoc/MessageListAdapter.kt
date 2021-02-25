package com.zk.lemopoc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.zk.lemopoc.databinding.ChatBubbleBotBinding
import com.zk.lemopoc.databinding.ChatBubbleUserBinding


enum class Types(val value: Int) {
    USER(1),
    BOT(2);
}

class MessageListAdapter :
   ListAdapter<Message, MultiViewViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiViewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            Types.USER.value -> {
                val binding = ChatBubbleUserBinding.inflate(inflater, parent, false)
                return UserMessageViewHolder(binding)
            }
            Types.BOT.value -> {
                val binding = ChatBubbleBotBinding.inflate(inflater, parent, false)
                return BotMessageViewHolder(binding)
            }
        }

        // TODO: 25/02/2021 Get rid of this
        val binding = ChatBubbleBotBinding.inflate(inflater, parent, false)
        return BotMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MultiViewViewHolder, position: Int) {
        holder.onBindVewHolder(position, getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.isUserMessage) {
            Types.USER.value
        } else {
            Types.BOT.value
        }
    }
}