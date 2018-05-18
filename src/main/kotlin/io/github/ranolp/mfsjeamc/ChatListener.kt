package io.github.ranolp.mfsjeamc

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object ChatListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onChat(event: AsyncPlayerChatEvent) {
        val chatter = Chatter(event.player)

        if (!chatter.useMfsjea) {
            return
        }

        event.isCancelled = true

        val converted = chatter.jeamfs(event.message)
        sendMessage(event.player.displayName, event.format, event.message, converted, event.recipients)
    }
}
