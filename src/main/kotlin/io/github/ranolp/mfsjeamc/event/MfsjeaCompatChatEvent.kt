package io.github.ranolp.mfsjeamc.event

import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent

class MfsjeaCompatChatEvent(async: Boolean, who: Player, message: String, players: Set<Player>) :
    AsyncPlayerChatEvent(async, who, message, players)
