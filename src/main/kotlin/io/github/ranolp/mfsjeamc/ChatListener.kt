package io.github.ranolp.mfsjeamc

import io.github.ranolp.mfsjeamc.event.MfsjeaCompatChatEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object ChatListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onChat(event: AsyncPlayerChatEvent) {
        if (event is MfsjeaCompatChatEvent) {
            return
        }

        val chatter = Chatter(event.player)

        if (!chatter.useMfsjea) {
            return
        }

        event.isCancelled = true

        val converted = chatter.jeamfs(event.message)

        val compatEvent =
            MfsjeaCompatChatEvent(event.isAsynchronous, event.player, converted.sentence, event.recipients)
        Bukkit.getPluginManager().callEvent(compatEvent)
        if (!compatEvent.isCancelled) {
            sendMessage(
                compatEvent.player.displayName,
                compatEvent.format,
                compatEvent.message,
                converted,
                compatEvent.recipients
            )
        }
    }
}
