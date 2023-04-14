package com.vla.sksu.app.utils

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

val TextInputEditText.inputLayout: TextInputLayout?
    get() = parent.parent as? TextInputLayout

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { word ->
    word.replaceFirstChar { char ->
        if (char.isLowerCase())
            char.titlecase(Locale.US)
        else
            char.toString()
    }
}