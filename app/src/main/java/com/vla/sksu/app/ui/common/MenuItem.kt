package com.vla.sksu.app.ui.common

data class MenuItem (
    val name: String,
    val count: Int = 0,
    val action: () -> Unit
)
