package uk.co.developmentanddinosaurs.stego.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class StegoCodegenPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val extension = project.extensions.create<StegoCodegenExtension>("stegoCodegen")

    project.tasks.register<GenerateDtosTask>("generateDtos") {
      dependsOn(kspTask(project))
      componentsFile.set(extension.componentsFile)
      baseComponentsFile.set(extension.baseComponentsFile)
      nodesFile.set(extension.nodesFile)
      outputDir.set(extension.outputDir)
    }

    project.tasks.register<GenerateMappersTask>("generateMappers") {
      dependsOn(kspTask(project))
      componentsFileProperty.set(extension.componentsFile)
      baseComponentsFileProperty.set(extension.baseComponentsFile)
      nodesFileProperty.set(extension.nodesFile)
      outputDir.set(extension.outputDir)
    }

    project.tasks.register<GenerateModuleTask>("generateModule") {
      dependsOn(kspTask(project))
      componentsFile.set(extension.componentsFile)
      outputDir.set(extension.outputDir)
    }
  }

  private fun kspTask(project: Project) = project.rootProject
    .project(":domain:ui-core")
    .tasks
    .named("kspKotlinJvm")
}
