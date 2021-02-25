package com.zk.lemopoc

import androidx.recyclerview.widget.DiffUtil

class ItemDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return /*oldItem.textInput == newItem.textInput
                && oldItem.isUserMessage == newItem.isUserMessage
                && oldItem.selection == newItem.selection
                && oldItem.numberInput == newItem.numberInput*/ false
    }
}