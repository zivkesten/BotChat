package com.zk.lemopoc

import com.idanatz.oneadapter.external.interfaces.Diffable
import java.util.*

open class Message(
    val textInput: String? = null
): Diffable {
    override val uniqueIdentifier: Long
        get() = UUID.randomUUID().mostSignificantBits

    override fun areContentTheSame(other: Any): Boolean {
        return other is UserMessage
                && textInput == other.textInput
    }
}