package com.vla.sksu.app.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryOverview (
    @Expose var pendings: Int? = null,
    @Expose var toReturn: Int? = null,
    @Expose var all: Int? = null,
) : Parcelable {
    companion object {
        const val KEY_PENDINGS = "pendings"
        const val KEY_TO_RETURN = "to_return"
        const val KEY_ALL = "all"
    }
}
