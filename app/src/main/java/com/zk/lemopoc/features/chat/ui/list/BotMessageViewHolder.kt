package com.zk.lemopoc.features.chat.ui.list

import com.zk.lemopoc.features.chat.models.Message
import com.zk.lemopoc.databinding.ChatBubbleBotBinding

class BotMessageViewHolder(
    private val binding: ChatBubbleBotBinding
) : MultiViewViewHolder(binding.root) {

    override fun onBindVewHolder(position: Int, multiViewItem: Message) {
        super.onBindVewHolder(position, multiViewItem)
        binding.textMessageBot.text = multiViewItem.textInput
    }
}