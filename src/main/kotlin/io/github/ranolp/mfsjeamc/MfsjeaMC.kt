@file:JvmName("MfsjeaMC")

package io.github.ranolp.mfsjeamc

import org.bukkit.plugin.java.JavaPlugin

class MfsjeaMC : JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(ChatListener, this)
    }

    override fun onDisable() {

    }
}
