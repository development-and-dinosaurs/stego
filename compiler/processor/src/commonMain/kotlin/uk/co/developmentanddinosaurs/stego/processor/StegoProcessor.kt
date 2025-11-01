package uk.co.developmentanddinosaurs.stego.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import uk.co.developmentanddinosaurs.stego.annotations.StegoNode

@OptIn(KspExperimental::class)
class StegoProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("Stego processor started")
        val nodeSymbols =
            resolver
                .getSymbolsWithAnnotation(StegoNode::class.qualifiedName!!)
                .filterIsInstance<KSClassDeclaration>()

        logger.info("Found ${nodeSymbols.count()} annotated classes")
        if (!nodeSymbols.iterator().hasNext()) {
            return emptyList()
        }

        writeNodeList(nodeSymbols)

        return emptyList()
    }

    private fun writeNodeList(nodeSymbols: Sequence<KSClassDeclaration>) {
        val outputFile =
            codeGenerator.createNewFile(
                dependencies =
                    Dependencies(
                        aggregating = true,
                        sources = nodeSymbols.mapNotNull { it.containingFile }.toList().toTypedArray(),
                    ),
                packageName = "",
                fileName = "stego_annotated_classes",
                extensionName = "txt",
            )
        outputFile.bufferedWriter().use { writer ->
            nodeSymbols.forEach { node ->
                writer.write(node.qualifiedName!!.asString())
                writer.newLine()
            }
        }
    }
}
