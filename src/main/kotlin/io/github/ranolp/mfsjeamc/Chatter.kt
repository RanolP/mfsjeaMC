package io.github.ranolp.mfsjeamc

import io.github.ranolp.mfsjea.ConversionResult
import io.github.ranolp.mfsjea.Mfsjea
import io.github.ranolp.mfsjea.escaper.BracketEscaper
import io.github.ranolp.mfsjea.keyboard.InputKeyboard
import io.github.ranolp.mfsjea.keyboard.OutputKeyboard
import io.github.ranolp.mfsjeamc.dao.ChatterDAO
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class Chatter private constructor(private val uuid: UUID) {
    companion object {
        private val CACHE = mutableMapOf<UUID, Chatter>()

        @JvmName("of")
        operator fun invoke(uuid: UUID) = CACHE.getOrPut(uuid) {
            Chatter(uuid)
        }

        @JvmName("of")
        operator fun invoke(player: Player) = invoke(player.uniqueId)
    }

    init {
        val data = ChatterDAO.get(uuid)
        data["useMfsjea"]?.takeIf { it is Boolean }?.let {
            useMfsjea = it as Boolean
        }
        data["specified-input"]?.takeIf { it is String }?.let {
            val name = it as String
            val keyboard = inputKeyboards.firstOrNull { it.name == name }
            if (keyboard == null) {
                Bukkit.getLogger().severe("입력 키보드 ${name}를 찾을 수 없습니다.")
            } else {
                inputKeyboard = keyboard
            }
        }
        data["specified-output"]?.takeIf { it is String }?.let {
            val name = it as String
            val keyboard = outputKeyboards.firstOrNull { it.name == name }
            if (keyboard == null) {
                Bukkit.getLogger().severe("출력 키보드 ${name}를 찾을 수 없습니다.")
            } else {
                outputKeyboard = keyboard
            }
        }
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

        mfsjea = mfsjea.extend(inputKeyboards = {
            if (inputKeyboard !== null) listOf(inputKeyboard) else it
        }, outputKeyboards = {
            if (outputKeyboard !== null) listOf(outputKeyboard) else it
        }, escapers = {
            listOf(BracketEscaper('[', ']'))
        })

        save()
    }

    fun save() {
        ChatterDAO.update(this)
        ChatterDAO.save()
    }

    fun serialize(): Map<String, Any?> = mapOf(
        "use-mfsjea" to useMfsjea,
        "specified-input" to inputKeyboard?.name,
        "specified-output" to outputKeyboard?.name
    )

    fun jeamfs(sentence: String): ConversionResult = mfsjea.jeamfsAuto(sentence)
}
