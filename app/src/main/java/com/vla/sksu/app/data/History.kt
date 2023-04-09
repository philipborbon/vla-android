package com.vla.sksu.app.data

import com.google.gson.annotations.Expose
import java.util.*

data class History (
    @Expose var id: Int? = null,
    @Expose var approved: Int? = null,
    @Expose var approvedAt: Date? = null,
    @Expose var category: Category? = null,
    @Expose var book: Book? = null,
    @Expose var createdAt: Date? = null,
    @Expose var updatedAt: Date? = null,
)
