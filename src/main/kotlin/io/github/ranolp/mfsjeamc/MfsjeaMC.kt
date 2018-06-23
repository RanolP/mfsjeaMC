@file:JvmName("MfsjeaMC")

package io.github.ranolp.mfsjeamc

import io.github.ranolp.mfsjeamc.command.EnkoCommand
import io.github.ranolp.mfsjeamc.command.KeyboardCommand
import io.github.ranolp.mfsjeamc.dao.ChatterDAO
import io.github.ranolp.mfsjeamc.web.UpdateChecker
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.io.File


class MfsjeaMC : JavaPlugin() {
    companion object {
        internal var releaseInfo: UpdateChecker.ReleaseInfo? = null

        fun getInstance(): MfsjeaMC = JavaPlugin.getPlugin(MfsjeaMC::class.java)
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(ChatListener, this)
        ChatterDAO.init(File(dataFolder, "userdata.yml"))

        getCommand("enko").executor = EnkoCommand
        getCommand("keyboard").executor = KeyboardCommand

        logger.info("${ChatColor.YELLOW}[#] ${ChatColor.WHITE}mfsjea의 업데이트를 확인합니다...")

        UpdateChecker.check(description, "RanolP", "mfsjeaMC", failure = {
            logger.info("${ChatColor.GREEN}[#] ${ChatColor.WHITE}mfsjea가 최신 버전입니다.")
        }) {
            releaseInfo = this

            logger.info("${ChatColor.GOLD}[!] ${ChatColor.WHITE}mfsjea의 새 업데이트가 있습니다.")
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

    override fun onDisable() {
        ChatterDAO.save()
    }
}
