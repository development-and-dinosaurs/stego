package uk.co.developmentanddinosaurs.stego.gradle

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

private val SERIALIZABLE_ANNOTATION = ClassName("kotlinx.serialization", "Serializable")
private val SERIAL_NAME_ANNOTATION = ClassName("kotlinx.serialization", "SerialName")


abstract class GenerateDtosTask : DefaultTask() {
  @get:InputFile
  abstract val componentsFile: RegularFileProperty

  @get:InputFile
  abstract val nodesFile: RegularFileProperty

  @get:InputFile
  abstract val baseComponentsFile: RegularFileProperty

  @get:OutputDirectory
  abstract val outputDir: DirectoryProperty

  @TaskAction
  fun generate() {
    val output = outputDir.get().asFile
    output.mkdirs()

    val components = getComponentMetadata(componentsFile.get().asFile) + getComponentMetadata(nodesFile.get().asFile)
    val baseComponents = getBaseComponentMetadata(baseComponentsFile.get().asFile)
    val baseComponentProperties = baseComponents.associate { it.qualifiedName to it.properties.toSet() }
    components.forEach { node ->
      val superTypeProps = baseComponentProperties[node.superType] ?: emptySet()
      generateDto(node, superTypeProps)
    }
  }

  private fun generateDto(node: ComponentMetadata, superTypeProps: Set<String>) {
    val dtoName = "${node.simpleName}Dto"
    val packageName = node.packageName.replace(".ui.", ".serialisation.ui.")

    val parameterSpecs = node.properties.map(::createParameterSpec)
    val propertySpecs = node.properties.map { prop -> createPropertySpec(prop, superTypeProps) }

    val constructor = FunSpec.constructorBuilder()
      .addParameters(parameterSpecs)
      .build()

    val dtoClass = TypeSpec.classBuilder(dtoName)
      .addModifiers(KModifier.DATA)
      .addAnnotation(SERIALIZABLE_ANNOTATION)
      .primaryConstructor(constructor)
      .addProperties(propertySpecs)
      .apply {
        node.stegoType.let { stegoType ->
          val serialName = AnnotationSpec.builder(SERIAL_NAME_ANNOTATION)
            .addMember("%S", stegoType)
            .build()
          addAnnotation(serialName)
        }
        node.superType?.let {
          addSuperinterface(ClassName.bestGuess(mapToDto(it)))
        }
      }
      .build()

    FileSpec.builder(packageName, dtoName)
      .addType(dtoClass)
      .build()
      .writeTo(outputDir.get().asFile)
  }

  private fun createParameterSpec(property: PropertyMetadata): ParameterSpec {
    val typeName = parseTypeName(property.typeQualifiedName)
    return ParameterSpec.builder(property.name, typeName).apply {
      if (typeName.isNullable) {
        defaultValue("%L", null)
      }
    }.build()
  }

  private fun createPropertySpec(property: PropertyMetadata, superTypeProps: Set<String>): PropertySpec {
    val typeName = parseTypeName(property.typeQualifiedName)
    return PropertySpec.builder(property.name, typeName)
      .initializer(property.name)
      .apply {
        if (superTypeProps.contains(property.name)) {
          addModifiers(KModifier.OVERRIDE)
        }
      }
      .build()
  }

}
