package io.github.ranolp.mfsjeamc.sender

import io.github.ranolp.mfsjea.ConversionResult
import io.github.ranolp.mfsjeamc.Chatter
import io.github.ranolp.mfsjeamc.event.MfsjeaCompatChatEvent
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.event.player.AsyncPlayerChatEvent

internal object TooltipMessageSender : MessageSender {
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

        val component = TextComponent(
            compatEvent.format.format(
                sender.asPlayer.displayName,
                compatEvent.message
            )
        )
        component.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            arrayOf(TextComponent("원문 : $original, ${converted.source.name} - ${converted.target.name}, 점수 : ${converted.score}").also {
                it.color = ChatColor.GRAY
                it.isItalic = true
            })
        )

        for (recipient in compatEvent.recipients) {
            recipient.spigot().sendMessage(ChatMessageType.CHAT, component)
        }
        Bukkit.getConsoleSender().sendMessage(component.text)
    }

}
