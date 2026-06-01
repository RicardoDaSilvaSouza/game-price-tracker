package com.gamepricetracker.core.domain.models

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class GamePriceTest {

    @Test
    fun `accepts a valid Currency object`() {
        val validCurrency = Currency(amount = 100.0, code = "USD")
        val gamePrice = GamePrice(name = "Bitcoin", currency = validCurrency)

        gamePrice.name shouldBe "Bitcoin"
        gamePrice.currency.amount shouldBe 100.0
    }
}
