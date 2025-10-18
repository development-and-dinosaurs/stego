pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "stego"
include(
    "core",
    "serialisation:core:kotlinx",
    "serialisation:ui:kotlinx",
    "ui:core",
    "ui:android",
    "app"
)