package com.zk.lemopoc.features.chat.ui.list.listItems

import com.zk.lemopoc.features.chat.models.Message
import com.zk.lemopoc.databinding.ChatBubbleUserBinding

class UserMessageViewHolder(
    private val binding: ChatBubbleUserBinding
) : MultiViewViewHolder(binding.root) {

    override fun onBindVewHolder(position: Int, multiViewItem: Message) {
        super.onBindVewHolder(position, multiViewItem)
        binding.textMessageUser.text = multiViewItem.textInput
    }
}