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
    "compiler:annotations",
    "compiler:processor",
    "data:serialisation:kotlinx:core",
    "data:serialisation:kotlinx:ui-core",
    "di:koin:kotlinx:core",
    "di:koin:kotlinx:ui",
    "domain:core",
    "domain:ui-core",
    "presentation:ui:android",
    "examples:android",
)
