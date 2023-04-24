package com.vla.sksu.app.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookMeta (
    @Expose var isAvailable: Boolean? = null,
    @Expose var limitReached: Boolean? = null,
    @Expose var activeBorrow: Int? = null,
    @Expose var lastRequestId: Int? = null,
    @Expose var lastRequestStatus: String? = null,
) : Parcelable {
    companion object {
        const val STATUS_PENDING = "pending"
        const val STATUS_RETURNED = "returned"
        const val STATUS_APPROVED = "approved"
        const val STATUS_DENIED = "denied"
    }
}
