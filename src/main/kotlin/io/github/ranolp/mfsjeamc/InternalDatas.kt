package io.github.ranolp.mfsjeamc

import io.github.ranolp.mfsjea.keyboard.*

internal val inputKeyboards = listOf(
    QwertyKeyboard,
    DvorakKeyboard,
    ColemakKeyboard
)

internal val inputKeyboardsMap = inputKeyboards.associateBy { it.name }

internal val outputKeyboards = listOf(
    DubeolStandardKeyboard,
    Sebeol390Keyboard,
    SebeolFinalKeyboard
)

internal val outputKeyboardsMap = outputKeyboards.associateBy { it.name }
