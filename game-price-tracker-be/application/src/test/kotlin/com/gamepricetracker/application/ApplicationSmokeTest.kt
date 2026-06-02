package com.gamepricetracker.application

import io.kotest.matchers.shouldBe
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.PostgreSQLContainer
import javax.sql.DataSource

@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationSmokeTest : TestPropertyProvider {

    @Inject lateinit var app: EmbeddedApplication<*>
    @Inject lateinit var dataSource: DataSource

    override fun getProperties(): Map<String, String> {
        postgres.start()
        return mapOf(
            "datasources.default.url" to postgres.jdbcUrl,
            "datasources.default.username" to postgres.username,
            "datasources.default.password" to postgres.password,
        )
    }

    @Test
    fun `Micronaut context starts`() {
        app.isRunning shouldBe true
    }

    @Test
    fun `Flyway applies V1 migration`() {
        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(
                    "SELECT success FROM flyway_schema_history WHERE version = '1'"
                )
                rs.next() shouldBe true
                rs.getBoolean("success") shouldBe true
            }
        }
    }

    companion object {
        private val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:18-alpine")
            .withDatabaseName("gpt")
            .withUsername("postgres")
            .withPassword("postgres")
    }
}
