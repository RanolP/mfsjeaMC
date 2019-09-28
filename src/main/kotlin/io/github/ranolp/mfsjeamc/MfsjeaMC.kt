@file:JvmName("MfsjeaMC")

package io.github.ranolp.mfsjeamc

import io.github.ranolp.mfsjeamc.command.EnkoCommand
import io.github.ranolp.mfsjeamc.command.KeyboardCommand
import io.github.ranolp.mfsjeamc.command.ReloadConfigCommand
import io.github.ranolp.mfsjeamc.dao.ChatterDAO
import io.github.ranolp.mfsjeamc.sender.MessageSender
import io.github.ranolp.mfsjeamc.web.UpdateChecker
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.io.File


class MfsjeaMC : JavaPlugin() {
    companion object {
        internal var releaseInfo: UpdateChecker.ReleaseInfo? = null

        fun getInstance(): MfsjeaMC = getPlugin(MfsjeaMC::class.java)
    }

    override fun onEnable() {
        loadConfig()

        server.pluginManager.registerEvents(ChatListener, this)
        ChatterDAO.init(File(dataFolder, "userdata.yml"))

        getCommand("enko").executor = EnkoCommand
        getCommand("keyboard").executor = KeyboardCommand
        getCommand("reloadconfig").executor = ReloadConfigCommand

        logger.info("${ChatColor.YELLOW}[…] ${ChatColor.WHITE}mfsjeaMC의 업데이트를 확인합니다...")

        UpdateChecker.check(description, "RanolP", "mfsjeaMC", failure = {
            if (it == UpdateChecker.FailureReason.ALREADY_LATEST) {
                logger.info("${ChatColor.GREEN}[✔] ${ChatColor.WHITE}mfsjeaMC가 최신 버전입니다.")
            } else {
                logger.severe(it.message)
            }
        }) {
            releaseInfo = this

            logger.info("${ChatColor.GOLD}[!] ${ChatColor.WHITE}mfsjeaMC의 새 업데이트가 있습니다.")
            logger.info("  ${ChatColor.GRAY}${description.version} → $version")
            logger.info("${ChatColor.AQUA}[업데이트 로그]")
            updateLog.split('\n').map { "  ${it.trim()}" }.forEach(logger::info)
            logger.info("${ChatColor.AQUA}[릴리즈 페이지]")
            logger.info("  $url")
            logger.info("${ChatColor.AQUA}[다운로드]")
            logger.info("  $downloadUrl")

            Bukkit.getOnlinePlayers().forEach(UpdateAlertListener::alert)

            server.pluginManager.registerEvents(UpdateAlertListener, this@MfsjeaMC)
        }
    }

    internal fun loadConfig() {
        saveDefaultConfig()
        reloadConfig()
        config.getKeys(true).forEach { key ->
            when (key) {
                "message-format.short-criteria" -> {
                    if (config.isInt(key)) {
                        Configuration.MessageFormat.shortCriteria = config.getInt(key)
                    } else {
                        logger.warning("$key must be integer")
                    }
                }
                "message-format.short", "message-format.long" -> {
                    if (config.isString(key)) {
                        MessageSender.Types.values().firstOrNull {
                            it.name == config.getString(key)
                        }?.also {
                            if (key.endsWith("long")) {
                                Configuration.MessageFormat.long = it
                            } else {
                                Configuration.MessageFormat.short = it
                            }
                        }
                    } else {
                        null
                    } ?: logger.warning("$key must be HIDE|NEXT|TOOLTIP")
                }
                "escaper.use" -> {
                    if (config.isBoolean(key)) {
                        Configuration.Escaper.use = config.getBoolean(key)
                    } else {
                        logger.warning("$key must be true|false")
                    }
                }
                "escaper.start", "escaper.end" -> {
                    if (config.isString(key)) {
                        config.getString(key).takeIf { it.length == 1 }?.also {
                            if (key.endsWith("end")) {
                                Configuration.Escaper.end = it[0]
                            } else {
                                Configuration.Escaper.start = it[0]
                            }
                        }
                    } else {
                        null
                    } ?: logger.warning("$key must be one character")
                }
            }
        }
    }

    override fun onDisable() {
        ChatterDAO.save()
    }
}
