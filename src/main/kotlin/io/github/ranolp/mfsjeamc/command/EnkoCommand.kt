package io.github.ranolp.mfsjeamc.command

import io.github.ranolp.mfsjea.Mfsjea
import io.github.ranolp.mfsjeamc.Chatter
import io.github.ranolp.mfsjeamc.createMessage
import net.md_5.bungee.api.ChatMessageType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object EnkoCommand : CommandExecutor {
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
            val converted = if (sender !is Player) {
                Mfsjea.DEFAULT.jeamfsAuto(args.joinToString(" "))
            } else {
                Chatter(sender).jeamfs(args.joinToString(" "))
            }

            val message = createMessage(
                "<${sender.name}> ${converted.sentence}",
                "${ChatColor.GRAY}${ChatColor.ITALIC}원문 : ${args.joinToString(" ")}, 점수 : ${converted.score}"
            )

            for (player in Bukkit.getOnlinePlayers()) {
                player.spigot().sendMessage(ChatMessageType.CHAT, message)
            }
        }
        return true
    }
}
