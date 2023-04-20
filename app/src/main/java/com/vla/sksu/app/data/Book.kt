package com.vla.sksu.app.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.vla.sksu.app.BuildConfig
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Book (
    @Expose var id: Int? = null,
    @Expose var categoryId: Int? = null,
    @Expose var title: String? = null,
    @Expose var language: String? = null,
    @Expose var circulation: String? = null,
    @Expose var description: String? = null,
    @Expose var image: String? = null,
    @Expose var author: String? = null,
    @Expose var publisher: String? = null,
    @Expose var datePublished: Date? = null,
    @Expose var pages: Int? = null,
    @Expose var category: Category? = null,
) : Parcelable {
    companion object {
        const val NOTIFICATION_TYPE = "book"
    }

    fun getImagePath() = "${BuildConfig.IMAGE_PATH}${image}"
}