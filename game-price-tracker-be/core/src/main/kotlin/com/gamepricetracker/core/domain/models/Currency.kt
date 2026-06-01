package com.gamepricetracker.core.domain.models

data class Currency(val amount: Double, val code: String) {
    init {
        require(amount > 0) { "Amount must be positive." }
    }

    fun isValid(): Boolean = amount > 0
}
