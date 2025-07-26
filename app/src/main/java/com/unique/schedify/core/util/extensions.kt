package com.unique.schedify.core.util

import androidx.core.graphics.toColorInt
import androidx.compose.ui.graphics.Color
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper


fun String?.toArgbColorInt(): Int? {
    return try {
        if (this.isNullOrBlank()) null
        else {
            val hex = this.removePrefix("#")
            val argbHex = when (hex.length) {
                6 -> "#FF$hex"       // Add full alpha
                8 -> "#$hex"         // Already ARGB
                else -> return null  // Invalid format
            }
            argbHex.toColorInt()
        }
    } catch (e: IllegalArgumentException) {
        null
    }
}


fun Pair<String?, String?>.toGradientColors(): List<Color> {
    val (lightHex, darkHex) = this

    val lightColorInt = lightHex.toArgbColorInt() ?: 0xFFFFFFFF.toInt()
    val darkColorInt = darkHex.toArgbColorInt() ?: 0xFFFFFFFF.toInt()

    return listOf(Color(lightColorInt), Color(darkColorInt))
}


fun Context.findActivity(): Activity {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    throw IllegalStateException("Activity not found from context")
}
