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
            when (args[0]) {
                "help" -> {
                    sender.sendMessage(
                            arrayOf(
                                    "${ChatColor.GREEN}[#] ${ChatColor.WHITE}명령어 도움말 - /${label}",
                                    "${ChatColor.YELLOW}* ${ChatColor.WHITE}/${label} ${ChatColor.AQUA}- ${ChatColor.WHITE}영한 자동 변환을 켜거나 끕니다.",
                                    "${ChatColor.YELLOW}* ${ChatColor.WHITE}/${label} ${ChatColor.GREEN}help ${ChatColor.AQUA}- ${ChatColor.WHITE}이 도움말을 보여줍니다.",
                                    "${ChatColor.YELLOW}* ${ChatColor.WHITE}/${label} ${ChatColor.GREEN}on/enable [-q]${if(sender.isOp) " [players...]" else ""} ${ChatColor.AQUA}- ${ChatColor.WHITE}영한 자동 변환을 켭니다.",
                                    "${ChatColor.YELLOW}* ${ChatColor.WHITE}/${label} ${ChatColor.GREEN}off/disable [-q]${if(sender.isOp) " [players...]" else ""} ${ChatColor.AQUA}- ${ChatColor.WHITE}영한 자동 변환을 끕니다.",
                                    "${ChatColor.YELLOW}* ${ChatColor.WHITE}/${label} ${ChatColor.GREEN}chat ${ChatColor.AQUA}- ${ChatColor.WHITE}메시지를 변환합니다.",
                                    "${ChatColor.YELLOW}* ${ChatColor.WHITE}/${label} ${ChatColor.GREEN}word ${ChatColor.AQUA}- ${ChatColor.WHITE}단어 단위 변환을 켜거나 끕니다."
                            )
                    )
                }
                "on", "enable" -> {
                    var quiet = false
                    val targets = mutableListOf<Player>()
                    var hasError = false
                    loop@ for (argument in args.drop(1)) {
                        when (argument) {
                            "-q" -> quiet = true
                            else -> {
                                if (!sender.isOp) {
                                    sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}잘못된 인자입니다: $argument")
                                    hasError = true
                                    continue@loop
                                }
                                val player = Bukkit.getPlayerExact(argument)
                                if (player != null) {
                                    targets += player
                                } else {
                                    sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}${argument}님은 현재 접속 중이지 않습니다.")
                                    hasError = true
                                }
                            }
                        }
                    }
                    if (hasError) {
                        return true
                    }
                    if (targets.isEmpty()) {
                        if (sender !is Player) {
                            sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}플레이어만 사용 가능한 명령어입니다.")
                            return true
                        }
                        targets += sender
                    }
                    for (target in targets) {
                        val chatter = Chatter(target)
                        if (!chatter.useMfsjea) {
                            chatter.useMfsjea = true
                            if (!quiet) {
                                chatter.asPlayer.sendMessage(
                                        "${ChatColor.GREEN}[#] ${ChatColor.WHITE}이제 영한 자동 변환을 사용합니다."
                                )
                            }
                        } else if (!quiet) {
                            chatter.asPlayer.sendMessage(
                                    "${ChatColor.GRAY}[#] ${ChatColor.WHITE}이미 영한 자동 변환을 사용하고 있습니다.")
                        }
                        chatter.save()
                    }
                }
                "off", "disable" -> {
                    var quiet = false
                    val targets = mutableListOf<Player>()
                    var hasError = false
                    for (argument in args.drop(1)) {
                        when (argument) {
                            "-q" -> quiet = true
                            else -> {
                                val player = Bukkit.getPlayerExact(argument)
                                if (player != null) {
                                    targets += player
                                } else {
                                    sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}${argument}님은 현재 접속 중이지 않습니다.")
                                    hasError = true
                                }
                            }
                        }
                    }
                    if (hasError) {
                        return true
                    }
                    if (targets.isEmpty()) {
                        if (sender !is Player) {
                            sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}플레이어만 사용 가능한 명령어입니다.")
                            return true
                        }
                        targets += sender
                    }
                    for (target in targets) {
                        val chatter = Chatter(target)
                        if (chatter.useMfsjea) {
                            chatter.useMfsjea = false
                            if (!quiet) {
                                chatter.asPlayer.sendMessage(
                                        "${ChatColor.GREEN}[#] ${ChatColor.WHITE}이제 영한 자동 변환을 사용하지 않습니다."
                                )
                            }
                        } else if (!quiet) {
                            chatter.asPlayer.sendMessage(
                                    "${ChatColor.GRAY}[#] ${ChatColor.WHITE}이미 영한 자동 변환을 사용하지 않고 있습니다.")
                        }
                        chatter.save()
                    }
                }
                "chat" -> {
                    val message = args.asSequence().drop(1).toList().joinToString(" ")
                    val converted = if (sender !is Player) {
                        if (message[0] == '!') mfsjeaForce.jeamfsAuto(message.substring(1))
                        else mfsjea.jeamfsAuto(message)
                    } else {
                        if (message[0] == '!') Chatter(sender).jeamfs(message.substring(1), true)
                        else Chatter(sender).jeamfs(message, false)
                    }

                    sendMessage(sender.name, "<%s> %s", args.joinToString(" "), converted, Bukkit.getOnlinePlayers())
                }
                "word" -> {
                    if (sender !is Player) {
                        sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}플레이어만 사용 가능한 명령어입니다.")
                        return true
                    }

                    val chatter = Chatter(sender)
                    chatter.byWord = !chatter.byWord
                    chatter.save()

                    sender.sendMessage(
                            if (chatter.useMfsjea) {
                                "${ChatColor.GREEN}[#] ${ChatColor.WHITE}이제 단어 단위 변환을 사용합니다."
                            } else {
                                "${ChatColor.GRAY}[#] ${ChatColor.WHITE}이제 단어 단위 변환을 사용하지 않습니다."
                            }
                    )
                }
                else -> {
                    sender.sendMessage("${ChatColor.RED}[!] ${ChatColor.WHITE}/${label} help로 도움말을 확인하세요")
                }
            }
        }
        return true
    }
}
