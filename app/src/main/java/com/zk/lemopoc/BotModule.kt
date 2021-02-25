package com.zk.lemopoc

import android.widget.TextView
import com.idanatz.oneadapter.external.modules.ItemModule

class BotModule : ItemModule<BotMessage>() {
    init {
        config {
            layoutResource = R.layout.chat_bubble_bot
        }
        onBind { model, viewBinder, metadata ->
            val title = viewBinder.findViewById<TextView>(R.id.text_message_bot)
            title.text = model.textInput
        }
        onUnbind { model, viewBinder, metadata ->
            // unbind logic like stop animation, release webview resources, etc.
        }
    }
}
