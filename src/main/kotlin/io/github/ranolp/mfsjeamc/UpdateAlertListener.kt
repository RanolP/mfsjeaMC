package io.github.ranolp.mfsjeamc

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*
import net.md_5.bungee.api.ChatColor as BungeeChatColor

object UpdateAlertListener : Listener {
    val notified = mutableListOf<UUID>()

    fun alert(player: Player) {
        if (!player.isOp || player.uniqueId in notified) {
            return
        }
        notified += player.uniqueId
        MfsjeaMC.releaseInfo?.let {
            player.sendMessage("${ChatColor.GOLD}[!] ${ChatColor.WHITE}mfsjeaMC의 새 업데이트가 있습니다.")
            player.sendMessage("  ${ChatColor.GRAY}${MfsjeaMC.getInstance().description.version} → ${it.version}")
            player.sendMessage("${ChatColor.AQUA}[업데이트 로그]")
            it.updateLog.split('\n').map { "  ${it.trim()}" }.forEach(player::sendMessage)
            val releasePage = TextComponent("릴리즈 페이지").apply {
                color = BungeeChatColor.AQUA
                isUnderlined = true
                hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    arrayOf(TextComponent("이동"))
                )
                clickEvent = ClickEvent(
                    ClickEvent.Action.OPEN_URL,
                    it.url
                )
            }
            val download = TextComponent("다운로드").apply {
                color = BungeeChatColor.AQUA
                isUnderlined = true
                hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    arrayOf(TextComponent("이동"))
                )
                clickEvent = ClickEvent(
                    ClickEvent.Action.OPEN_URL,
                    it.downloadUrl
                )
            }
            player.spigot().sendMessage(ChatMessageType.CHAT, releasePage, TextComponent(", "), download)
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        alert(event.player)
    }
}
