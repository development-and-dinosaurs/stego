plugins {
  id("com.diffplug.spotless")
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
