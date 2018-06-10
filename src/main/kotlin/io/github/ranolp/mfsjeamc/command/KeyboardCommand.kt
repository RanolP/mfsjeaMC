package io.github.ranolp.mfsjeamc.command

import io.github.ranolp.mfsjeamc.*
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

object KeyboardCommand : TabExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}플레이어만 사용 가능한 명령어입니다.")
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}/$label <input/output> [키보드 레이아웃 이름]로 사용해주세요!")
            return true
        }
        when (args[0]) {
            "input" -> {
                when {
                    args.size == 1 -> {
                        Chatter(sender).inputKeyboard = null
                        sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}입력 키보드 레이아웃을 초기화했습니다.")
                    }
                    args[1] in inputKeyboardsMap -> {
                        Chatter(sender).inputKeyboard = inputKeyboardsMap[args[1]]
                        sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}이제부터 입력 키보드 레이아웃은 ${args[1]}입니다!")
                    }
                    else -> sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}올바른 키보드 레이아웃 이름을 입력하세요. ${ChatColor.GRAY}(가능 레이아웃 이름 : ${inputKeyboardsMap.keys})")
                }
            }
            "output" -> {
                when {
                    args.size == 1 -> {
                        Chatter(sender).outputKeyboard = null
                        sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}출력 키보드 레이아웃을 초기화했습니다.")
                    }
                    args[1] in outputKeyboardsMap -> {
                        Chatter(sender).outputKeyboard = outputKeyboardsMap[args[1]]
                        sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}이제부터 출력 키보드 레이아웃은 ${args[1]}입니다!")
                    }
                    else -> sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}올바른 키보드 레이아웃 이름을 입력하세요. ${ChatColor.GRAY}(가능 레이아웃 이름 : ${outputKeyboardsMap.keys})")
                }
            }
            else -> {
                sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}/$label <input/output> [키보드 레이아웃 이름]로 사용해주세요!")
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        return if (args.isEmpty()) {
            listOf("input", "output")
        } else {
            when (args.size) {
                1 -> listOf("input", "output").filter { it.startsWith(args[0]) }
                2 -> when {
                    args[0] == "input" -> inputKeyboards.map { it.name }.filter { it.startsWith(args[1]) }
                    args[0] == "output" -> outputKeyboards.map { it.name }.filter { it.startsWith(args[1]) }
                    else -> emptyList()
                }
                else -> emptyList()
            }
        }
    }
}
