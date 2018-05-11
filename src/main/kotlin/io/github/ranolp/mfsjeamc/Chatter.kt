package io.github.ranolp.mfsjeamc

import io.github.ranolp.mfsjea.ConversionResult
import io.github.ranolp.mfsjea.Mfsjea
import io.github.ranolp.mfsjea.escaper.BracketEscaper
import io.github.ranolp.mfsjea.keyboard.InputKeyboard
import io.github.ranolp.mfsjea.keyboard.OutputKeyboard
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class Chatter private constructor(private val uuid: UUID) {
    companion object {
        private val CACHE = mutableMapOf<UUID, Chatter>()

        @JvmName("of")
        operator fun invoke(uuid: UUID) = CACHE.getOrPut(uuid) {
            Chatter(
                uuid
            )
        }

        @JvmName("of")
        operator fun invoke(player: Player) = invoke(player.uniqueId)
    }

    @get:JvmName("useMfsjea")
    @set:JvmName("useMfsjea")
    var useMfsjea: Boolean = true

    @set:JvmName("specify")
    var inputKeyboard: InputKeyboard? = null
        set(value) {
            field = value
            updateMfsjea()
        }
    @set:JvmName("specify")
    var outputKeyboard: OutputKeyboard? = null
        set(value) {
            field = value
            updateMfsjea()
        }

    @get:JvmName("asPlayer")
    val asPlayer: Player
        get() = Bukkit.getPlayer(uuid)

    private var mfsjea = Mfsjea.DEFAULT

    private fun updateMfsjea() {
        val inputKeyboard = this.inputKeyboard
        val outputKeyboard = this.outputKeyboard

        var mfsjea = Mfsjea.DEFAULT.extend(escapers = {
            listOf(BracketEscaper('[', ']'))
        })

        if (inputKeyboard !== null) mfsjea = mfsjea.extend(inputKeyboards = {
            listOf(inputKeyboard)
        })
        if (outputKeyboard !== null) mfsjea = mfsjea.extend(outputKeyboards = {
            listOf(outputKeyboard)
        })

        this.mfsjea = mfsjea
    }

    fun save() {

    }

    fun jeamfs(sentence: String): ConversionResult = mfsjea.jeamfsAuto(sentence)
}
