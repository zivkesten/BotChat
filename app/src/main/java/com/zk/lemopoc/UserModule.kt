package com.zk.lemopoc

import android.widget.TextView
import com.idanatz.oneadapter.external.modules.ItemModule

class UserModule : ItemModule<UserMessage>() {
    init {
        config {
            layoutResource = R.layout.chat_bubble_user
        }
        onBind { model, viewBinder, metadata ->
            val title = viewBinder.findViewById<TextView>(R.id.text_message_user)
            title.text = model.textInput
        }
        onUnbind { model, viewBinder, metadata ->
            // unbind logic like stop animation, release webview resources, etc.
        }
    }
}
