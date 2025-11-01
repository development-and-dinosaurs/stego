plugins {
    id("com.diffplug.spotless")
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint().editorConfigOverride(
            mapOf(
                "ktlint_standard_class-signature" to "disabled",
                "ktlint_standard_multiline-expression-wrapping" to "disabled",
                "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
            ),
        )
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}
