import kotlinx.kover.gradle.plugin.dsl.tasks.KoverXmlReport
import org.sonarqube.gradle.SonarTask

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlinx.kover) apply false
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

sonarqube {
    properties {
        property("sonar.organization", "development-and-dinosaurs")
        property("sonar.projectKey", "development-and-dinosaurs_stego")
        property("sonar.coverage.jacoco.xmlReportPaths", "${rootDir}/**/build/reports/kover/report.xml")
        property("sonar.coverage.exclusions", "examples/**")
    }
}

tasks.withType<SonarTask> {
    dependsOn(subprojects.map { it.tasks.withType<KoverXmlReport>() })
}
