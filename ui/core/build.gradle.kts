import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.diffplug.spotless)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    android {
        namespace = "uk.co.developmentanddinosaurs.stego.ui.core"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions.jvmTarget = JvmTarget.JVM_11
            }
        }
    }
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
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

tasks.withType<Test> {
    useJUnitPlatform()
    filter {
        isFailOnNoMatchingTests = false
    }
}
