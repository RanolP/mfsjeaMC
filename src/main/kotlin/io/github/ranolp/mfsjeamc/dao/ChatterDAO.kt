package io.github.ranolp.mfsjeamc.dao

import io.github.ranolp.mfsjeamc.Chatter
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

object ChatterDAO {
    private lateinit var file: File
    private lateinit var yaml: YamlConfiguration

    fun init(file: File) {
        this.file = file
        this.yaml = YamlConfiguration.loadConfiguration(this.file)
    }

    fun update(chatter: Chatter) {
        yaml.set(chatter.asPlayer.uniqueId.toString(), chatter.serialize())
    }

    fun save() {
        yaml.save(file)
    }

    internal fun get(uuid: UUID): Map<String, Any?> = yaml.get(uuid.toString()) as? Map<String, Any?> ?: emptyMap()
}
