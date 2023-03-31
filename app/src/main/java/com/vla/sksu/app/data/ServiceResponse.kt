package com.vla.sksu.app.data

data class ServiceResponse <T> (
    var data: T? = null,
    var errorString: String? = null,
    var success: Boolean = false,
    var status: Int? = null,
    var error: Throwable? = null,
) {
    fun getErrorMessage(): String? {
        errorString?.let {
            return errorString
        }

        error?.let {
            return it.localizedMessage
        } ?: run {
            return "Status Code: $status"
        }
    }
}