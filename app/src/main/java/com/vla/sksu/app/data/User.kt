package com.vla.sksu.app.data

import com.google.gson.annotations.Expose

data class User (
    @Expose var id: Int? = null,
    @Expose var name: String? = null,
    @Expose var libraryId: String? = null,
)