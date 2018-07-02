package io.github.ranolp.mfsjeamc

import io.github.ranolp.mfsjeamc.sender.MessageSender

internal object Configuration {
    object MessageFormat {
        var shortCriteria = 1
        var short: MessageSender.Types = MessageSender.Types.NEXT
        var long: MessageSender.Types = MessageSender.Types.TOOLTIP
    }

    object Escaper {
        var use: Boolean = true
        var start = '['
        var end = ']'
    }
}
