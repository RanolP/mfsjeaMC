package io.github.ranolp.mfsjeamc

import io.github.ranolp.mfsjeamc.event.MfsjeaCompatChatEvent
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

        if (event.message[0] == '^') {
            event.message = event.message.substring(1)
            return
        }

        val converted =
            if (event.message[0] == '!') chatter.jeamfs(event.message.substring(1), true)
            else chatter.jeamfs(event.message, false)

        val type =
            if (event.message.split(' ').size <= Configuration.MessageFormat.shortCriteria) {
                Configuration.MessageFormat.short
            } else {
                Configuration.MessageFormat.long
            }

        type.sender.sendMessage(
            chatter,
            event,
            event.message,
            converted
        )
    }
}
