package uk.co.developmentanddinosaurs.stego.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import kotlinx.serialization.modules.SerializersModule
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateModuleTask : DefaultTask() {
    @get:InputFile
    abstract val componentsFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val output = outputDir.get().asFile
        output.mkdirs()

        val components = getComponentMetadata(componentsFile.get().asFile)
        val module = generateSerializersModule(components)
        module.writeTo(output)
    }

    private fun generateSerializersModule(
        metadataList: List<ComponentMetadata>
    ): FileSpec {
        val superTypeToSubclasses = metadataList
            .filter { it.superType != null }
            .groupBy { it.superType!! }

        val polymorphicMember = MemberName("kotlinx.serialization.modules", "polymorphic")
        val subclassMember = MemberName("kotlinx.serialization.modules", "subclass")

        val serializersModuleFunction = FunSpec.builder("uiSerializersModule")
            .returns(SerializersModule::class)
            .addStatement("val %L = %T {", "module", SerializersModule::class)
            .addStatement("%M(%T::class) {", polymorphicMember, ClassName("uk.co.developmentanddinosaurs.stego.serialisation", "StateDto"))
            .addStatement("  %M(%T::class)", subclassMember, ClassName("uk.co.developmentanddinosaurs.stego.serialisation.ui", "UiStateDto"))
            .addStatement("}")
            .also { funSpecBuilder ->
                    superTypeToSubclasses.forEach { (superType, subClasses) ->
                        val dto = ClassName.bestGuess(mapToDto(superType))
                        funSpecBuilder.addStatement("%M(%T::class) {", polymorphicMember, dto)
                        subClasses.forEach { subClass ->
                            val dto = ClassName.bestGuess(mapToDto(subClass.qualifiedName))
                            funSpecBuilder.addStatement("  %M(%T::class)", subclassMember, dto)
                        }
                        funSpecBuilder.addStatement("  }")
                    }
            }
            .addStatement("}")
            .addStatement("return %L", "module")

        return FileSpec.builder("uk.co.developmentanddinosaurs.stego.serialisation.ui.module", "StegoUiSerializersModule")
            .addFunction(serializersModuleFunction.build())
            .build()
    }

}
