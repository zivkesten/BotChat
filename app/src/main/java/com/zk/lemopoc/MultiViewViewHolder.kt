package com.zk.lemopoc

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class MultiViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    open fun onBindVewHolder(position:Int, multiViewItem: Message) {

    }

    open fun onViewDetached(position: Int, multiViewItem: Message) {

    }

}