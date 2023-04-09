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

        status?.let {
            return "Status Code: $status"
        }

        error?.let {
            return it.localizedMessage
        }

        return "Unknown issue occurred."
    }
}