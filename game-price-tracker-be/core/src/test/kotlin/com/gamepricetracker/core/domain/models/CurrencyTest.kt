package com.gamepricetracker.core.domain.models

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CurrencyTest {

    @Test
    fun `throws when amount is zero`() {
        val ex = shouldThrow<IllegalArgumentException> {
            Currency(amount = 0.0, code = "USD")
        }
        ex.message shouldBe "Amount must be positive."
    }

    @Test
    fun `throws when amount is negative`() {
        val ex = shouldThrow<IllegalArgumentException> {
            Currency(amount = -10.0, code = "USD")
        }
        ex.message shouldBe "Amount must be positive."
    }

    @Test
    fun `is valid when amount is positive`() {
        val currency = Currency(amount = 100.0, code = "USD")
        currency.isValid() shouldBe true
    }
}
