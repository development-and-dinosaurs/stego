import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import uk.co.developmentanddinosaurs.stego.gradle.GenerateDtosTask
import uk.co.developmentanddinosaurs.stego.gradle.GenerateMappersTask
import uk.co.developmentanddinosaurs.stego.gradle.GenerateModuleTask

plugins {
  `spotless-convention`
  id("uk.co.developmentanddinosaurs.stego.codegen")
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.kotlin.multiplatform.library)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.kotlinx.kover)
}

stegoCodegen {
  componentsFile.set(
      rootProject
          .project(":domain:ui-core")
          .layout
          .buildDirectory
          .file("generated/ksp/jvm/jvmMain/resources/stego/components.json"),
  )
  baseComponentsFile.set(
      rootProject
          .project(":domain:ui-core")
          .layout
          .buildDirectory
          .file("generated/ksp/jvm/jvmMain/resources/stego/base-components.json"),
  )
  outputDir.set(layout.buildDirectory.dir("generated/sources/kotlin"))
}

kotlin {
  android {
    namespace = "uk.co.developmentanddinosaurs.stego.serialisation.ui.kotlinx"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()

    compilations.configureEach {
      compileTaskProvider.configure { compilerOptions.jvmTarget = JvmTarget.JVM_17 }
    }
  }
  jvm()

  sourceSets {
    commonMain {
      kotlin.srcDir(tasks.named<GenerateDtosTask>("generateDtos").map { it.outputDir })
      kotlin.srcDir(tasks.named<GenerateMappersTask>("generateMappers").map { it.outputDir })
      kotlin.srcDir(tasks.named<GenerateModuleTask>("generateModule").map { it.outputDir})
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

    jvmTest.dependencies { implementation(libs.kotest.runner.junit5) }
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
  filter { isFailOnNoMatchingTests = false }
}
