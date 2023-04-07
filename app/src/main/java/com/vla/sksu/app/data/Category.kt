package com.vla.sksu.app.data

import com.google.gson.annotations.Expose

data class Category (
    @Expose var id: Int? = null,
    @Expose var name: String? = null,
    @Expose var books: Int? = null,
    @Expose var categories: Int? = null,
)