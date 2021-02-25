package com.zk.lemopoc

import com.zk.lemopoc.databinding.ChatBubbleBotBinding

class BotMessageViewHolder(
    private val binding: ChatBubbleBotBinding
) : MultiViewViewHolder(binding.root) {

    override fun onBindVewHolder(position: Int, multiViewItem: BotMessage) {
        super.onBindVewHolder(position, multiViewItem)
        binding.textMessageBot.text = multiViewItem.textInput
    }
}