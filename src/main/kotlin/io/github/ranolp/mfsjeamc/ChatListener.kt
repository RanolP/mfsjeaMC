package io.github.ranolp.mfsjeamc

import io.github.ranolp.mfsjeamc.event.MfsjeaCompatChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerChatTabCompleteEvent

object ChatListener : Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onCompletion(event: PlayerChatTabCompleteEvent) {

        if (event.lastToken.isNullOrEmpty()) {
            return
        }

        val chatter = Chatter(event.player)

        if(event.tabCompletions.isNotEmpty()) {return
}

        val converted =
                if (event.lastToken[0] == '!') event.lastToken.substring(1).let { chatter.jeamfsList(it, true) }
                else event.lastToken.let { chatter.jeamfsList(it, false) }

        event.tabCompletions += converted.sortedByDescending {
            it.score
        }.filter {
            chatter.inputKeyboard != null || chatter.outputKeyboard != null || it.score > 0
        }.map {
            it.sentence
        }.distinct()

        if(event.lastToken !in event.tabCompletions) {
            event.tabCompletions += event.lastToken
        }
    }
}
