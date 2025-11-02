
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class NodeMetadata(
    val qualifiedName: String,
    val simpleName: String,
    val type: String,
    val properties: List<PropertyMetadata>
)

@Serializable
private data class PropertyMetadata(
    val name: String,
    val typeQualifiedName: String
)

abstract class GenerateDtosTask : DefaultTask() {
    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val input = inputFile.get().asFile
        val output = outputDir.get().asFile
        output.deleteRecursively()
        output.mkdirs()

        val json = Json { ignoreUnknownKeys = true }
        val nodes = json.decodeFromString<List<NodeMetadata>>(input.readText())

        nodes.forEach { node ->
            val dtoName = "${node.simpleName}Dto"
            val packageName = "uk.co.developmentanddinosaurs.stego.serialisation.ui.node"
            val serializableAnnotation = ClassName("kotlinx.serialization", "Serializable")
            val uiNodeDtoInterface = ClassName("uk.co.developmentanddinosaurs.stego.serialisation.ui.node", "UiNodeDto")
            val serialNameAnnotation = AnnotationSpec.builder(ClassName("kotlinx.serialization", "SerialName"))
                .addMember("%S", node.type)
                .build()


            val constructorBuilder = FunSpec.constructorBuilder()
            val propertySpecs = node.properties.map { property ->
                val typeName = parseTypeName(property.typeQualifiedName)
                constructorBuilder.addParameter(property.name, typeName)
                val propertySpec = PropertySpec.builder(property.name, typeName)
                    .initializer(property.name)

                if (property.name == "id") {
                    propertySpec.addModifiers(KModifier.OVERRIDE)
                }

                propertySpec.build()
            }

            val dtoClass = TypeSpec.classBuilder(dtoName)
                .addModifiers(KModifier.DATA)
                .addAnnotation(serializableAnnotation)
                .addAnnotation(serialNameAnnotation)
                .primaryConstructor(constructorBuilder.build())
                .addSuperinterface(uiNodeDtoInterface)
                .addProperties(propertySpecs)
                .build()
 
            val fileSpec = FileSpec.builder(packageName, dtoName)
                .addType(dtoClass)
                .build()
 
            fileSpec.writeTo(output)
        }
    }

    private fun parseTypeName(typeString: String): TypeName {
        val trimmed = typeString.trim()
        if (trimmed == "*") {
            return STAR
        }

        val genericStartIndex = trimmed.indexOf('<')
        if (genericStartIndex == -1) {
            return ClassName.bestGuess(mapToDto(trimmed))
        }

        val genericEndIndex = trimmed.lastIndexOf('>')
        if (genericEndIndex == -1) {
            return ClassName.bestGuess(trimmed)
        }

        val rawTypeString = trimmed.take(genericStartIndex)
        val rawClassName = ClassName.bestGuess(mapToDto(rawTypeString))

        val argsString = trimmed.substring(genericStartIndex + 1, genericEndIndex)

        val typeArgs = mutableListOf<TypeName>()
        var nestLevel = 0
        var lastSplit = 0

        argsString.forEachIndexed { index, char ->
            if (char == '<') nestLevel++
            if (char == '>') nestLevel--
            if (char == ',' && nestLevel == 0) {
                typeArgs.add(parseTypeName(argsString.substring(lastSplit, index)))
                lastSplit = index + 1
            }
        }
        typeArgs.add(parseTypeName(argsString.substring(lastSplit)))

        return rawClassName.parameterizedBy(typeArgs)
    }

    private fun mapToDto(typeString: String): String {
        val typeMappings = mapOf(
            "uk.co.developmentanddinosaurs.stego.ui.node.UiNode" to "uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto",
            "uk.co.developmentanddinosaurs.stego.ui.node.ButtonAction" to "uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonActionDto"
        )
        println("Mapping $typeString to ${typeMappings[typeString]}")
        return typeMappings[typeString] ?: typeString
    }
}
