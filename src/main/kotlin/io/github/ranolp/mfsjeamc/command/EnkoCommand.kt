package io.github.ranolp.mfsjeamc.command

import io.github.ranolp.mfsjea.Mfsjea
import io.github.ranolp.mfsjea.escaper.BracketEscaper
import io.github.ranolp.mfsjea.grader.AsciiGrader
import io.github.ranolp.mfsjea.grader.IncompleteWordGrader
import io.github.ranolp.mfsjeamc.Chatter
import io.github.ranolp.mfsjeamc.sendMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object EnkoCommand : CommandExecutor {
    val mfsjea = Mfsjea.DEFAULT.extend(escapers = {
        listOf(BracketEscaper('[', ']'))
    })
    val mfsjeaForce = mfsjea.extend(graders = {
        it - AsciiGrader - IncompleteWordGrader
    })

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (args.isEmpty()) {
            if (sender !is Player) {
                sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}플레이어만 사용 가능한 명령어입니다.")
                return true
            }

            val chatter = Chatter(sender)
            chatter.useMfsjea = !chatter.useMfsjea
            chatter.save()

            sender.sendMessage(
                if (chatter.useMfsjea) {
                    "${ChatColor.GREEN}[#] ${ChatColor.WHITE}이제 영한 자동 변환을 사용합니다."
                } else {
                    "${ChatColor.GRAY}[#] ${ChatColor.WHITE}이제 영한 자동 변환을 사용하지 않습니다."
                }
            )
        } else {
            val message = args.joinToString(" ")
            val converted = if (sender !is Player) {
                if (message[0] == '!') mfsjeaForce.jeamfsAuto(message.substring(1))
                else mfsjea.jeamfsAuto(message)
            } else {
                if (message[0] == '!') Chatter(sender).jeamfs(message.substring(1), true)
                else Chatter(sender).jeamfs(message, false)
            }

            sendMessage(sender.name, "<%s> %s", args.joinToString(" "), converted, Bukkit.getOnlinePlayers())
        }
        return true
    }
}
