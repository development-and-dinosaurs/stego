plugins {
    `spotless-convention`
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":compiler:annotations"))
            implementation(libs.symbol.processing.api)
        }
    }
}
