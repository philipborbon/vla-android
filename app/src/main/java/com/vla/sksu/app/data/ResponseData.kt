package com.vla.sksu.app.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponseData<D, M>(
    @Expose var data: D? = null,
    @Expose @SerializedName("_meta") var meta: M? = null,
)