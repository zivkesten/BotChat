package com.zk.lemopoc.features.chat.ui.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.zk.lemopoc.features.chat.models.Message

abstract class MultiViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun onBindVewHolder(position:Int, multiViewItem: Message) {}
}