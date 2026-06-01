package com.gamepricetracker.core.domain.models

data class GamePrice(val name: String, val currency: Currency) {
    init {
        require(currency.isValid()) { "GamePrice requires a valid Currency object." }
    }
}
