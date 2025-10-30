import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    android {
        namespace = "uk.co.developmentanddinosaurs.stego.di.koin.kotlinx.ui"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
            }
        }
    }
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":di:koin:kotlinx:core"))
                api(project(":data:serialisation:kotlinx:core"))
                api(project(":data:serialisation:kotlinx:ui-core"))
                api(libs.kotlinx.serialization.json)
                api(libs.koin.core)
            }
        }
    }
}
