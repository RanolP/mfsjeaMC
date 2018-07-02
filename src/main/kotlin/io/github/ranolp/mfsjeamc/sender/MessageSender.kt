package io.github.ranolp.mfsjeamc.sender

import io.github.ranolp.mfsjea.ConversionResult
import io.github.ranolp.mfsjeamc.Chatter
import org.bukkit.event.player.AsyncPlayerChatEvent

internal interface MessageSender {
    enum class Types(val sender: MessageSender) {
        HIDE(HideMessageSender),
        NEXT(NextMessageSender),
        TOOLTIP(TooltipMessageSender)
    }

    fun sendMessage(
        sender: Chatter,
        event: AsyncPlayerChatEvent,
        original: String,
        converted: ConversionResult
    )
}
