package io.github.ranolp.mfsjeamc

import net.md_5.bungee.api.ChatMessageType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
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
        val message = createMessage(
            event.format.format(event.player.displayName, converted.sentence),
            "${ChatColor.GRAY}${ChatColor.ITALIC}원문 : ${event.message}, ${converted.source.name} - ${converted.target.name}, 점수 : ${converted.score}"
        )

        for (recipient in event.recipients) {
            recipient.spigot().sendMessage(ChatMessageType.CHAT, message)
        }
        Bukkit.getConsoleSender().sendMessage(event.format.format(event.player.displayName, converted.sentence))
    }
}
