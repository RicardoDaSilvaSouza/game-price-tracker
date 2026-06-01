import java.util.Properties

plugins {
    `kotlin-dsl`
}

fun rootProperties(): Properties {
    val props = Properties()
    file("../gradle.properties").inputStream().use { props.load(it) }
    return props
}

val rootProps = rootProperties()
val kotlinVersion: String = rootProps.getProperty("kotlinVersion")
val micronautGradlePluginVersion: String = rootProps.getProperty("micronautGradlePluginVersion")

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")
    implementation("io.micronaut.gradle:micronaut-gradle-plugin:$micronautGradlePluginVersion")
}
