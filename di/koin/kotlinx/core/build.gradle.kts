import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `spotless-convention`
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
  android {
    namespace = "uk.co.developmentanddinosaurs.stego.di.koin.kotlinx.core"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
    compilations.configureEach {
      compileTaskProvider.configure { compilerOptions.jvmTarget.set(JvmTarget.JVM_17) }
    }
  }
  jvm()

  sourceSets {
    commonMain.dependencies {
      api(project(":data:serialisation:kotlinx:core"))
      api(libs.kotlinx.serialization.json)
      api(libs.koin.core)
    }
  }
}
