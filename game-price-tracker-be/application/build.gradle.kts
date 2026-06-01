plugins {
    id("micronaut-conventions")
    id("io.micronaut.application")
}

micronaut {
    version.set(providers.gradleProperty("micronautVersion"))
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.gamepricetracker.*")
    }
}

application {
    mainClass.set("com.gamepricetracker.application.ApplicationKt")
}

val postgresDriverVersion = providers.gradleProperty("postgresDriverVersion").get()

dependencies {
    implementation(project(":core"))
    implementation(project(":web"))
    implementation(project(":infra"))

    // SQL + Flyway + Data JPA (Hibernate) — versions resolved by Micronaut Platform BOM
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    "kapt"("io.micronaut.data:micronaut-data-processor")

    runtimeOnly("org.postgresql:postgresql:$postgresDriverVersion")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
}
