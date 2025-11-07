package uk.co.developmentanddinosaurs.stego.gradle

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty

abstract class StegoCodegenExtension {
    abstract val componentsFile: RegularFileProperty
    abstract val baseComponentsFile: RegularFileProperty
    abstract val outputDir: DirectoryProperty
}
