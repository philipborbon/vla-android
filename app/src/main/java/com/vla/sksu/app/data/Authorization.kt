package com.vla.sksu.app.data

import com.google.gson.annotations.Expose


data class Authorization (
    @Expose var token: String? = null,
    @Expose var tokentype: String? = "Bearer",
) {
    fun getAccessToken(): String {
        return "$tokentype $token"
    }
}