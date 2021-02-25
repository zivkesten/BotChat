package com.zk.lemopoc.features.chat.ui

import androidx.recyclerview.widget.DiffUtil
import com.zk.lemopoc.Message

class ItemDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.textInput == newItem.textInput
                && oldItem.isUserMessage == newItem.isUserMessage
    }
}