import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
  alias(libs.plugins.kotlin.serialization)
  id("com.diffplug.spotless") version "8.0.0"
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.kotlinpoet)
  implementation(libs.spotless.gradle.plugin)
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
  compilerOptions.jvmTarget = JvmTarget.JVM_17
}

gradlePlugin {
  plugins {
    create("stegoCodegen") {
      id = "uk.co.developmentanddinosaurs.stego.codegen"
      implementationClass = "uk.co.developmentanddinosaurs.stego.gradle.StegoCodegenPlugin"
    }
  }
}

spotless {
  kotlin {
    target("src/**/*.kt")
    ktfmt()
  }
  kotlinGradle {
    target("*.gradle.kts")
    ktfmt()
  }
}
