package uk.co.developmentanddinosaurs.stego.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateMappersTask : DefaultTask() {
    @get:InputFile
    abstract val componentsFileProperty: RegularFileProperty

    @get:InputFile
    abstract val baseComponentsFileProperty: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDirectoryProperty: DirectoryProperty

    private lateinit var rootTypeToMapperInterface: Map<String, ClassName>

    @TaskAction
    fun generate() {
        val componentsFile = componentsFileProperty.get().asFile
        val baseComponentsFile = baseComponentsFileProperty.get().asFile
        val outputDirectory = outputDirectoryProperty.get().asFile
        outputDirectory.mkdirs()

        rootTypeToMapperInterface = createMapperInterfaces(baseComponentsFile)

        val components = getComponentMetadata(componentsFile)
        components.forEach { node ->
            val mapperSpec = generateMapper(node)
            mapperSpec.writeTo(outputDirectory)
        }
    }

    private fun createMapperInterfaces(baseComponentsFile: File): Map<String, ClassName> {
        val baseComponents = getBaseComponentMetadata(baseComponentsFile)
        return baseComponents.associate {
            val mapperPackage = it.packageName.replaceFirst(".ui", ".serialisation.ui") + ".mapper"
            val mapperInterfaceName = "${it.simpleName}Mapper"
            it.qualifiedName to ClassName(mapperPackage, mapperInterfaceName)
        }
    }

    private fun generateMapper(node: ComponentMetadata): FileSpec {
        val mapperName = "${node.simpleName}Mapper"
        val mapperPackageName = node.qualifiedName.replaceFirst(".ui", ".serialisation.ui")
            .substringBeforeLast('.')
            .plus(".mapper")

        val componentClassName = ClassName.bestGuess(node.qualifiedName)
        val dtoPackageName = node.qualifiedName.replaceFirst(".ui", ".serialisation.ui").substringBeforeLast('.')
        val dtoClassName = ClassName(dtoPackageName, "${node.simpleName}Dto")

        val constructorBuilder = FunSpec.constructorBuilder()
        val propertySpecs = mutableListOf<PropertySpec>()

        val mappingStatements = node.properties.joinToString(",\n") { property ->
            val rhs = buildPropertyMapping(
                property = property,
                constructorBuilder = constructorBuilder,
                propertySpecs = propertySpecs
            )
            "${property.name} = $rhs"
        }

        val primaryConstructor = constructorBuilder.build()
        val mapperClass = TypeSpec.classBuilder(mapperName)
            .primaryConstructor(primaryConstructor)
            .addProperties(propertySpecs)

        val superType = node.superType
        if (superType != null) {
            val mapperInterface = rootTypeToMapperInterface.getValue(superType)
            val dtoInterface = ClassName.bestGuess(mapToDto(superType))
            val domainInterface = ClassName.bestGuess(superType)

            val mapFunction = FunSpec.builder("map")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("dto", dtoInterface)
                .returns(domainInterface)
                .addStatement("require(dto is %T)", dtoClassName)
                .addStatement("return %T(\n%L\n)", componentClassName, mappingStatements)
                .build()

            mapperClass.addSuperinterface(mapperInterface)
            mapperClass.addFunction(mapFunction)
        } else {
            val mapFunction = FunSpec.builder("map")
                .addParameter("dto", dtoClassName)
                .returns(componentClassName)
                .addStatement("return %T(\n%L\n)", componentClassName, mappingStatements)
                .build()
            mapperClass.addFunction(mapFunction)
        }

        return FileSpec.builder(mapperPackageName, mapperName)
            .addType(mapperClass.build())
            .build()
    }

    private fun buildPropertyMapping(
        property: PropertyMetadata,
        constructorBuilder: FunSpec.Builder,
        propertySpecs: MutableList<PropertySpec>
    ): String {
        val propertyName = property.name
        val typeName = parseTypeName(property.typeQualifiedName)

        var typeToInspect = typeName.copy(nullable = false)
        var isCollection = false

        if (typeName is ParameterizedTypeName && typeName.rawType == LIST) {
            typeToInspect = typeName.typeArguments.first()
            isCollection = true
        }

        require(typeToInspect is ClassName)

        if (!typeToInspect.packageName.contains("stego")) {
            return "dto.$propertyName"
        }

        val mapperType = typeToInspect.packageName + ".mapper." + typeToInspect.simpleName.replace("Dto", "Mapper")
        val mapperClassName = ClassName.bestGuess(mapperType)
        val mapperFieldName = mapperClassName.simpleName.replaceFirstChar { it.lowercase() }
        constructorBuilder.addParameter(mapperFieldName, mapperClassName)
        propertySpecs.add(
            PropertySpec.builder(mapperFieldName, mapperClassName, KModifier.PRIVATE)
                .initializer(mapperFieldName)
                .build()
        )

        return if (isCollection) {
            "dto.$propertyName.map { $mapperFieldName.map(it) }"
        } else {
            "$mapperFieldName.map(dto.$propertyName)"
        }
    }
}
