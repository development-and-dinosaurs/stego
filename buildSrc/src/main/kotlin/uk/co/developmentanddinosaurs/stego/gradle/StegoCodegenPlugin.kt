package uk.co.developmentanddinosaurs.stego.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class StegoCodegenPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create<StegoCodegenExtension>("stegoCodegen")

        project.tasks.register<GenerateDtosTask>("generateDtos") {
            dependsOn(project.rootProject
                .project(":domain:ui-core")
                .tasks
                .named("kspKotlinJvm"))
            componentsFile.set(extension.componentsFile)
            baseComponentsFile.set(extension.baseComponentsFile)
            outputDir.set(extension.dtoOutputDir)
        }

        project.tasks.register<GenerateMappersTask>("generateMappers") {
            dependsOn(project.rootProject
                .project(":domain:ui-core")
                .tasks
                .named("kspKotlinJvm"))
            componentsFileProperty.set(extension.componentsFile)
            baseComponentsFileProperty.set(extension.baseComponentsFile)
            outputDirectoryProperty.set(extension.mapperOutputDir)
        }
    }
}
