package com.zk.lemopoc.features.chat.ui

import com.zk.lemopoc.Message
import com.zk.lemopoc.databinding.ChatBubbleBotBinding

class BotMessageViewHolder(
    private val binding: ChatBubbleBotBinding
) : MultiViewViewHolder(binding.root) {

    override fun onBindVewHolder(position: Int, multiViewItem: Message) {
        super.onBindVewHolder(position, multiViewItem)
        binding.textMessageBot.text = multiViewItem.textInput
    }
}