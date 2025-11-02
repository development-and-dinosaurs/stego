
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `spotless-convention`
    `generate-dtos`
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinx.kover)
}

val generateDtos =
    tasks.register<Generate_dtos_gradle.GenerateDtosTask>("generateDtos") {
        group = "generation"
        description = "Generate DTO classes from metadata"
        dependsOn(
            rootProject
                .project(":domain:ui-core")
                .tasks
                .named("kspKotlinJvm"),
        )

        inputFile.set(
            rootProject
                .project(":domain:ui-core")
                .layout.buildDirectory
                .file("generated/ksp/jvm/jvmMain/resources/stego/nodes.json"),
        )
        outputDir.set(layout.buildDirectory.dir("generated/sources/dtos/kotlin"))
    }

kotlin {
    android {
        namespace = "uk.co.developmentanddinosaurs.stego.serialisation.ui.kotlinx"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()

        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions.jvmTarget = JvmTarget.JVM_17
            }
        }
    }
    jvm()

    sourceSets {
        commonMain {
            kotlin.srcDir(generateDtos.map { it.outputDir })
        }
        commonMain.dependencies {
            implementation(project(":domain:core"))
            implementation(project(":domain:ui-core"))
            implementation(project(":data:serialisation:kotlinx:core"))
            api(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
        }
        commonTest.dependencies {
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.assertions.json)
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
