import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `spotless-convention`
  alias(libs.plugins.android.kotlin.multiplatform.library)
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.ksp)
}

kotlin {
  android {
    namespace = "uk.co.developmentanddinosaurs.stego.ui.core"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()

    compilations.configureEach {
      compileTaskProvider.configure { compilerOptions.jvmTarget = JvmTarget.JVM_17 }
    }
  }
  jvm()

  sourceSets {
    commonMain.dependencies {
      implementation(project(":domain:core"))
      implementation(project(":compiler:annotations"))
    }
    commonTest.dependencies {
      implementation(libs.kotest.assertions.core)
      implementation(libs.kotest.framework.engine)
    }

    jvmTest.dependencies { implementation(libs.kotest.runner.junit5) }
  }
}

dependencies { "kspJvm"(project(":compiler:processor")) }

tasks.withType<Test> {
  useJUnitPlatform()
  filter { isFailOnNoMatchingTests = false }
}
