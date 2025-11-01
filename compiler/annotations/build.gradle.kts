plugins {
    `spotless-convention`
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()
}
