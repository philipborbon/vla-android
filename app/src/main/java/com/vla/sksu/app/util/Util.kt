package com.vla.sksu.app.util


class Util {
    companion object {
        const val FLAVOR_PASSENGER = "passenger"
        const val FLAVOR_DRIVER = "driver"

        fun formatAmount(amount: Double?): String {
            return String.format("Php %,.2f", amount)
        }
    }
}