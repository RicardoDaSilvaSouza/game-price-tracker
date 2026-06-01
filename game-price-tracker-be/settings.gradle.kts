plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "game-price-tracker-be"

include(
    "application",
    "core",
    "web",
    "infra",
)
