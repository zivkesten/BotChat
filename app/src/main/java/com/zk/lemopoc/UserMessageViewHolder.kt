package com.zk.lemopoc

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zk.lemopoc.databinding.ChatBubbleUserBinding

class UserMessageViewHolder(
    private val binding: ChatBubbleUserBinding
) : MultiViewViewHolder(binding.root) {

    override fun onBindVewHolder(position: Int, multiViewItem: Message) {
        super.onBindVewHolder(position, multiViewItem)
        binding.textMessageUser.text = multiViewItem.textInput
    }
}