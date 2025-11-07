import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `spotless-convention`
  alias(libs.plugins.android.kotlin.multiplatform.library)
  alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
  jvm()
  android {
    namespace = "uk.co.developmentanddinosaurs.stego.annotations"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()

    compilations.configureEach {
      compileTaskProvider.configure { compilerOptions.jvmTarget = JvmTarget.JVM_17 }
    }
  }
}
