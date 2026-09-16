pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "tbot-ruler"

include ("tbot-ruler-service", "tbot-ruler-console", "tbot-ruler-dev-db")
