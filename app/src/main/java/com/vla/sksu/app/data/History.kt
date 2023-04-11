package com.vla.sksu.app.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class History (
    @Expose var id: Int? = null,
    @Expose var approved: Int? = null,
    @Expose var approvedAt: Date? = null,
    @Expose var category: Category? = null,
    @Expose var book: Book? = null,
    @Expose var createdAt: Date? = null,
    @Expose var updatedAt: Date? = null,
) : Parcelable {
    companion object {
        const val NOTIFICATION_TYPE = "history"
    }
}
