plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.diffplug.spotless)
}

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":ui:core"))
            implementation(project(":serialisation:core:kotlinx"))
            api(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.framework.engine)
        }

        jvmTest.dependencies {
            implementation(libs.kotest.runner.junit5)
        }
    }
}

tasks.withType<Test>() {
    useJUnitPlatform()
    filter {
        isFailOnNoMatchingTests = false
    }
}
