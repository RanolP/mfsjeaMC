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

        if (event.message.isNullOrEmpty()) {
            return
        }

        val chatter = Chatter(event.player)

        if (!chatter.useMfsjea) {
            return
        }

        if (event.message[0] == '^') {
            event.message = if (event.message.length == 1) {
                "^"
            } else {
                event.message.substring(1)
            }
            return
        }

        val (original, converted) =
                if (event.message[0] == '!') event.message.substring(1).let { Pair(it, chatter.jeamfs(it, true)) }
                else event.message.let { Pair(it, chatter.jeamfs(it, false)) }

        if (converted.sentence == original) {
            event.message = original
            return
        }

        val type =
            if (event.message.split(' ').size <= Configuration.MessageFormat.shortCriteria) {
                Configuration.MessageFormat.short
            } else {
                Configuration.MessageFormat.long
            }

        type.sender.sendMessage(
            chatter,
            event,
            original,
            converted
        )
    }
}
