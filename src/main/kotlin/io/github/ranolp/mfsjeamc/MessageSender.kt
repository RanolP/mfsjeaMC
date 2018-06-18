package io.github.ranolp.mfsjeamc

import io.github.ranolp.mfsjea.ConversionResult
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun sendMessage(
    senderName: String,
    format: String,
    original: String,
    converted: ConversionResult,
    recipients: Iterable<Player>
) {
    val component = TextComponent(format.format(senderName, converted.sentence))
    if (original != converted.sentence) {
        if (!original.contains(' ')) {
            component.addExtra(TextComponent(original).also {
                it.color = ChatColor.GRAY
                it.isItalic = true
            })
        } else {
            component.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                arrayOf(TextComponent("원문 : $original, ${converted.source.name} - ${converted.target.name}, 점수 : ${converted.score}").also {
                    it.color = ChatColor.GRAY
                    it.isItalic = true
                })
            )
        }
    }

    for (recipient in recipients) {
        recipient.spigot().sendMessage(ChatMessageType.CHAT, component)
    }
    Bukkit.getConsoleSender().sendMessage(component.text)
}
