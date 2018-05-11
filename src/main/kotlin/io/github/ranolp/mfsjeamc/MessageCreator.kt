package io.github.ranolp.mfsjeamc

import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent


fun createMessage(message: String, hover: String): TextComponent {
    val component = TextComponent(message)
    component.hoverEvent = HoverEvent(
        HoverEvent.Action.SHOW_TEXT,
        arrayOf(TextComponent(hover))
    )
    return component
}
