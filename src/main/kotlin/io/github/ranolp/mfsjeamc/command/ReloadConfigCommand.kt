package io.github.ranolp.mfsjeamc.command

import io.github.ranolp.mfsjeamc.MfsjeaMC
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

object ReloadConfigCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        sender.sendMessage("${ChatColor.AQUA}[…] ${ChatColor.WHITE}설정 파일을 다시 불러옵니다.")
        MfsjeaMC.getInstance().loadConfig()
        sender.sendMessage("${ChatColor.GREEN}[✔] ${ChatColor.WHITE}설정 파일을 다시 불러왔습니다.")
        return true
    }
}
