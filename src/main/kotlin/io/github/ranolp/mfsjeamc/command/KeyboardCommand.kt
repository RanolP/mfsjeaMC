package io.github.ranolp.mfsjeamc.command

import io.github.ranolp.mfsjeamc.Chatter
import io.github.ranolp.mfsjeamc.inputKeyboardsMap
import io.github.ranolp.mfsjeamc.outputKeyboardsMap
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
            sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}/$label <input/output> [키보드 레이아웃 이름] 혹은 /$label state로 사용해주세요!")
            return true
        }
        val name = if (args.size > 1) args.copyOfRange(1, args.size).joinToString(" ") else ""
        when (args[0]) {
            "input" -> {
                when {
                    args.size == 1 -> {
                        Chatter(sender).inputKeyboard = null
                        sender.sendMessage("${ChatColor.GREEN}[#] ${ChatColor.WHITE}입력 키보드 레이아웃을 초기화했습니다.")
                    }
                    name in inputKeyboardsMap -> {
                        Chatter(sender).inputKeyboard = inputKeyboardsMap[name]
                        sender.sendMessage("${ChatColor.GREEN}[#] ${ChatColor.WHITE}이제부터 입력 키보드 레이아웃은 ${Chatter(sender).inputKeyboard?.name}입니다!")
                    }
                    else -> sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}올바른 키보드 레이아웃 이름을 입력하세요. ${ChatColor.GRAY}(가능 레이아웃 이름 : ${inputKeyboardsMap.keys})")
                }
            }
            "output" -> {
                when {
                    args.size == 1 -> {
                        Chatter(sender).outputKeyboard = null
                        sender.sendMessage("${ChatColor.GREEN}[#] ${ChatColor.WHITE}출력 키보드 레이아웃을 초기화했습니다.")
                    }
                    name in outputKeyboardsMap -> {
                        Chatter(sender).outputKeyboard = outputKeyboardsMap[name]
                        sender.sendMessage("${ChatColor.GREEN}[#] ${ChatColor.WHITE}이제부터 출력 키보드 레이아웃은 ${Chatter(sender).outputKeyboard?.name}입니다!")
                    }
                    else -> sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}올바른 키보드 레이아웃 이름을 입력하세요. ${ChatColor.GRAY}(가능 레이아웃 이름 : ${outputKeyboardsMap.keys})")
                }
            }
            "state" -> {
                val chatter = Chatter(sender)
                sender.sendMessage("${ChatColor.GREEN}[#] ${ChatColor.WHITE}당신의 키보드 상태")
                sender.sendMessage("   ${ChatColor.GRAY}입력 키보드 ${chatter.inputKeyboard?.name ?: "없음"}")
                sender.sendMessage("   ${ChatColor.GRAY}출력 키보드 ${chatter.outputKeyboard?.name ?: "없음"}")
            }
            else -> {
                sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}/$label <input/output> [키보드 레이아웃 이름] 혹은 /$label state로 사용해주세요!")
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
                    args[0] == "input" -> inputKeyboardsMap.keys.filter {
                        it.toLowerCase().startsWith(args[1].toLowerCase())
                    }
                    args[0] == "output" -> outputKeyboardsMap.keys.filter {
                        it.toLowerCase().startsWith(args[1].toLowerCase())
                    }
                    else -> emptyList()
                }
                else -> emptyList()
            }
        }
    }
}
