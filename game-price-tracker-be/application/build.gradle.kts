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
val testcontainersVersion = providers.gradleProperty("testcontainersVersion").get()

dependencies {
    implementation(project(":core"))
    implementation(project(":web"))
    implementation(project(":infra"))

    // JSON — required by the Netty HTTP server for error responses
    implementation("io.micronaut:micronaut-jackson-databind")

    // SQL + Flyway — versions resolved by Micronaut Platform BOM.
    // Hibernate JPA is intentionally absent: no @Entity classes exist yet. Add
    // micronaut-data-hibernate-jpa + micronaut-data-processor (kapt) when the
    // first JPA entity is introduced.
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.flyway:micronaut-flyway")

    runtimeOnly("org.postgresql:postgresql:$postgresDriverVersion")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.yaml:snakeyaml")

    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
}

tasks.withType<Test>().configureEach {
    // Pin a modern Docker API version. docker-java's DefaultDockerClientConfig reads this from the
    // `api.version` system property; recent daemons (incl. OrbStack) reject the default 1.32.
    systemProperty("api.version", "1.45")

    // If an OrbStack socket exists, point Testcontainers at it explicitly via DOCKER_HOST and force
    // the env-var-aware strategy (overriding any docker.client.strategy lock in ~/.testcontainers.properties).
    // Other Docker setups (CI, Docker Desktop, plain Linux) fall through to Testcontainers' auto-discovery.
    val orbstackSocket = file("${System.getProperty("user.home")}/.orbstack/run/docker.sock")
    if (orbstackSocket.exists()) {
        environment("DOCKER_HOST", "unix://${orbstackSocket.absolutePath}")
        environment(
            "TESTCONTAINERS_DOCKER_CLIENT_STRATEGY",
            "org.testcontainers.dockerclient.EnvironmentAndSystemPropertyClientProviderStrategy"
        )
    }
}
