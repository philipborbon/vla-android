package com.vla.sksu.app.data

import com.google.gson.annotations.Expose


data class User (
    @Expose var id: Int? = null,
    @Expose var firstname: String? = null,
    @Expose var lastname: String? = null,
    @Expose var email: String? = null,
    @Expose var usertype: String? = null
)