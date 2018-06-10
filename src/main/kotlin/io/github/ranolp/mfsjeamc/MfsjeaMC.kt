@file:JvmName("MfsjeaMC")

package io.github.ranolp.mfsjeamc

import io.github.ranolp.mfsjeamc.command.EnkoCommand
import io.github.ranolp.mfsjeamc.command.KeyboardCommand
import io.github.ranolp.mfsjeamc.dao.ChatterDAO
import org.bukkit.plugin.java.JavaPlugin
import java.io.File


class MfsjeaMC : JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(ChatListener, this)
        ChatterDAO.init(File(dataFolder, "userdata.yml"))

        getCommand("enko").executor = EnkoCommand
        getCommand("keyboard").executor = KeyboardCommand
    }

    override fun onDisable() {
        ChatterDAO.save()
    }
}
