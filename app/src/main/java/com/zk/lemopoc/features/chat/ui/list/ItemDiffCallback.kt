package com.zk.lemopoc.features.chat.ui.list

import androidx.recyclerview.widget.DiffUtil
import com.zk.lemopoc.features.chat.models.Message

class ItemDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.textInput == newItem.textInput
                && oldItem.messageType == newItem.messageType
    }
}