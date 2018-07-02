package io.github.ranolp.mfsjeamc.sender

import io.github.ranolp.mfsjea.ConversionResult
import io.github.ranolp.mfsjeamc.Chatter
import io.github.ranolp.mfsjeamc.event.MfsjeaCompatChatEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.player.AsyncPlayerChatEvent

internal object NextMessageSender : MessageSender {
    override fun sendMessage(
        sender: Chatter,
        event: AsyncPlayerChatEvent,
        original: String,
        converted: ConversionResult
    ) {
        event.isCancelled = true
        val compatEvent = MfsjeaCompatChatEvent(
            event.isAsynchronous,
            event.player,
            converted.sentence,
            event.recipients
        )
        Bukkit.getPluginManager().callEvent(compatEvent)
        if (compatEvent.isCancelled) {
            return
        }

        val message = compatEvent.format.format(
            sender.asPlayer.displayName,
            compatEvent.message
        )
        val messageForPlayer = message + ChatColor.GRAY + ChatColor.ITALIC + original

        for (recipient in compatEvent.recipients) {
            recipient.sendMessage(messageForPlayer)
        }
        Bukkit.getConsoleSender().sendMessage(message)
    }
}
