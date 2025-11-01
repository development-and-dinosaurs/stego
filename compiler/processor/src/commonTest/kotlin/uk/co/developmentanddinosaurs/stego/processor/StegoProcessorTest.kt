package uk.co.developmentanddinosaurs.stego.processor

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import com.tschuchort.compiletesting.kspSourcesDir
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import uk.co.developmentanddinosaurs.stego.processor.metadata.NodeInfo
import uk.co.developmentanddinosaurs.stego.processor.metadata.PropertyInfo
import java.io.File

@OptIn(ExperimentalCompilerApi::class)
class StegoProcessorTest :
    BehaviorSpec({
        Given("a source file with one class annotated with @StegoNode") {
            val source =
                SourceFile.kotlin(
                    "MyTestNode.kt",
                    """
                package stego.tests

                import uk.co.developmentanddinosaurs.stego.annotations.StegoNode
                @StegoNode("test.node")
                data class MyTestNode(val id: String, val name: String)
                """,
                )

            val compilation =
                KotlinCompilation().apply {
                    sources = listOf(source)
                    configureKsp {
                        symbolProcessorProviders += mutableListOf(StegoProcessorProvider())
                    }
                    inheritClassPath = true
                }

            When("the processor is run during compilation") {
                val result = compilation.compile()

                Then("the compilation should be successful") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                }

                And("it should generate a metadata JSON file") {
                    val generatedFile = File(compilation.kspSourcesDir, "resources/META-INF/stego/nodes.json")
                    generatedFile.exists() shouldBe true
                }

                And("the generated JSON should contain the correct metadata") {
                    val generatedFile = File(compilation.kspSourcesDir, "resources/META-INF/stego/nodes.json")
                    val nodes = Json.decodeFromString<List<NodeInfo>>(generatedFile.readText())

                    nodes.size shouldBe 1
                    val nodeInfo = nodes.first()

                    nodeInfo.qualifiedName shouldBe "stego.tests.MyTestNode"
                    nodeInfo.simpleName shouldBe "MyTestNode"
                    nodeInfo.type shouldBe "test.node"
                    nodeInfo.properties shouldBe
                        listOf(
                            PropertyInfo(
                                name = "id",
                                typeQualifiedName = "kotlin.String",
                            ),
                            PropertyInfo(
                                name = "name",
                                typeQualifiedName = "kotlin.String",
                            ),
                        )
                }
            }
        }

        Given("a source file with multiple classes annotated with @StegoNode") {
            val source =
                SourceFile.kotlin(
                    "MultipleNodes.kt",
                    """
                package stego.tests

                import uk.co.developmentanddinosaurs.stego.annotations.StegoNode

                @StegoNode("node.one")
                data class NodeOne(val id: String)

                @StegoNode("node.two")
                data class NodeTwo(val count: Int)
                """,
                )

            val compilation =
                KotlinCompilation().apply {
                    sources = listOf(source)
                    configureKsp {
                        symbolProcessorProviders += mutableListOf(StegoProcessorProvider())
                    }
                    inheritClassPath = true
                }

            When("the processor is run") {
                val result = compilation.compile()

                Then("the compilation should succeed") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                }

                And("the generated JSON should contain metadata for both classes") {
                    val generatedFile = File(compilation.kspSourcesDir, "resources/META-INF/stego/nodes.json")
                    val nodes = Json.decodeFromString<List<NodeInfo>>(generatedFile.readText())

                    nodes.size shouldBe 2
                    nodes.any { it.simpleName == "NodeOne" && it.type == "node.one" } shouldBe true
                    nodes.any { it.simpleName == "NodeTwo" && it.type == "node.two" } shouldBe true
                }
            }
        }

        Given("a source file with no classes annotated with @StegoNode") {
            val source =
                SourceFile.kotlin(
                    "Unannotated.kt",
                    """
                package stego.tests

                data class UnannotatedClass(val name: String)
                """,
                )

            val compilation =
                KotlinCompilation().apply {
                    sources = listOf(source)
                    configureKsp {
                        symbolProcessorProviders += mutableListOf(StegoProcessorProvider())
                    }
                    inheritClassPath = true
                }

            When("the processor is run") {
                val result = compilation.compile()

                Then("the compilation should succeed") {
                    result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                }

                And("it should not generate a metadata file") {
                    val generatedFile = File(compilation.kspSourcesDir, "resources/META-INF/stego/nodes.json")
                    generatedFile.exists() shouldBe false
                }
            }
        }
    })
