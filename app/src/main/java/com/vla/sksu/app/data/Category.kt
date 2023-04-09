package com.vla.sksu.app.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category (
    @Expose var id: Int? = null,
    @Expose var name: String? = null,
    @Expose var path: String? = null,
    @Expose var books: Int? = null,
    @Expose var categories: Int? = null,
) : Parcelable