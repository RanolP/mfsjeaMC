package io.github.ranolp.mfsjeamc

import io.github.ranolp.mfsjea.ConversionResult
import io.github.ranolp.mfsjea.Mfsjea
import io.github.ranolp.mfsjea.escaper.BracketEscaper
import io.github.ranolp.mfsjea.grader.AsciiGrader
import io.github.ranolp.mfsjea.grader.IncompleteWordGrader
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
        data["use-mfsjea"]?.takeIf { it is Boolean }?.let {
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
        updateMfsjea()
    }

    @get:JvmName("useMfsjea")
    @set:JvmName("useMfsjea")
    var useMfsjea: Boolean = true
        set(value) {
            field = value
            save()
        }

    @set:JvmName("specify")
    var inputKeyboard: InputKeyboard? = null
        set(value) {
            field = value
            updateMfsjea()
            save()
        }
    @set:JvmName("specify")
    var outputKeyboard: OutputKeyboard? = null
        set(value) {
            field = value
            updateMfsjea()
            save()
        }

    @get:JvmName("asPlayer")
    val asPlayer: Player
        get() = Bukkit.getPlayer(uuid)

    private lateinit var mfsjea: Mfsjea
    private lateinit var mfsjeaForce: Mfsjea

    private fun updateMfsjea() {
        mfsjea = Mfsjea.DEFAULT.extend(inputKeyboards = {
            inputKeyboard?.let(::listOf) ?: it
        }, outputKeyboards = {
            outputKeyboard?.let(::listOf) ?: it
        }, escapers = {
            if (Configuration.Escaper.use) {
                listOf(
                    BracketEscaper(
                        Configuration.Escaper.start,
                        Configuration.Escaper.end
                    )
                )
            } else {
                emptyList()
            }
        })
        mfsjeaForce = mfsjea.extend(graders = {
            it - AsciiGrader - IncompleteWordGrader
        })
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

    fun jeamfs(sentence: String, force: Boolean): ConversionResult =
        (if (force) mfsjeaForce else mfsjea).jeamfsAuto(sentence)
}
