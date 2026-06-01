plugins {
    id("kotlin-conventions")
    kotlin("kapt")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
}

val micronautVersion = providers.gradleProperty("micronautVersion").get()

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
    annotation("io.micronaut.aop.Around")
}

noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

dependencies {
    // Platform BOM resolves consistent versions across Micronaut satellite modules
    "kapt"(platform("io.micronaut.platform:micronaut-platform:$micronautVersion"))
    "kapt"("io.micronaut:micronaut-inject-java")
    "kapt"("io.micronaut.validation:micronaut-validation-processor")

    implementation(platform("io.micronaut.platform:micronaut-platform:$micronautVersion"))
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("jakarta.annotation:jakarta.annotation-api")
    runtimeOnly("ch.qos.logback:logback-classic")
}
