package com.zk.lemopoc.features.chat.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.zk.lemopoc.Message

abstract class MultiViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    open fun onBindVewHolder(position:Int, multiViewItem: Message) {

    }

    open fun onViewDetached(position: Int, multiViewItem: Message) {

    }

}