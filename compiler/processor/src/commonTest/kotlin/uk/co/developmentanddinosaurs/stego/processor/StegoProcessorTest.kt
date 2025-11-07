package uk.co.developmentanddinosaurs.stego.processor

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import com.tschuchort.compiletesting.kspSourcesDir
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.io.File
import kotlinx.serialization.json.Json
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import uk.co.developmentanddinosaurs.stego.processor.metadata.BaseComponentMetadata
import uk.co.developmentanddinosaurs.stego.processor.metadata.ComponentMetadata
import uk.co.developmentanddinosaurs.stego.processor.metadata.PropertyInfo

@OptIn(ExperimentalCompilerApi::class)
class StegoProcessorTest : BehaviorSpec() {
  init {
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
            configureKsp { symbolProcessorProviders += mutableListOf(StegoProcessorProvider()) }
            inheritClassPath = true
          }

      When("the processor is run during compilation") {
        val result = compilation.compile()

        Then("the compilation should be successful") {
          result.exitCode shouldBe KotlinCompilation.ExitCode.OK
        }

        And("it should generate a metadata JSON file") {
          val generatedFile = File(compilation.kspSourcesDir, "resources/stego/components.json")
          generatedFile.exists() shouldBe true
        }

        And("the generated JSON should contain the correct metadata") {
          val generatedFile = File(compilation.kspSourcesDir, "resources/stego/components.json")
          val components = Json.decodeFromString<List<ComponentMetadata>>(generatedFile.readText())

          components.size shouldBe 1
          val nodeInfo = components.first()

          nodeInfo.qualifiedName shouldBe "stego.tests.MyTestNode"
          nodeInfo.simpleName shouldBe "MyTestNode"
          nodeInfo.stegoType shouldBe "test.node"
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
              "MultipleComponents.kt",
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
            configureKsp { symbolProcessorProviders += mutableListOf(StegoProcessorProvider()) }
            inheritClassPath = true
          }

      When("the processor is run") {
        val result = compilation.compile()

        Then("the compilation should succeed") {
          result.exitCode shouldBe KotlinCompilation.ExitCode.OK
        }

        And("the generated JSON should contain metadata for both classes") {
          val generatedFile = File(compilation.kspSourcesDir, "resources/stego/components.json")
          val components = Json.decodeFromString<List<ComponentMetadata>>(generatedFile.readText())

          components.size shouldBe 2
          components.any { it.simpleName == "NodeOne" && it.stegoType == "node.one" } shouldBe true
          components.any { it.simpleName == "NodeTwo" && it.stegoType == "node.two" } shouldBe true
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
            configureKsp { symbolProcessorProviders += mutableListOf(StegoProcessorProvider()) }
            inheritClassPath = true
          }

      When("the processor is run") {
        val result = compilation.compile()

        Then("the compilation should succeed") {
          result.exitCode shouldBe KotlinCompilation.ExitCode.OK
        }

        And("it should not generate a metadata file") {
          val generatedFile = File(compilation.kspSourcesDir, "resources/stego/components.json")
          generatedFile.exists() shouldBe false
        }
      }
    }

    Given("a source file with a class annotated with @StegoNode and generic type arguments") {
      val source =
          SourceFile.kotlin(
              "GenericNode.kt",
              """
                package stego.tests

                import uk.co.developmentanddinosaurs.stego.annotations.StegoNode

                @StegoNode("generic.node")
                data class GenericNode(val id: String, val data: List<String>)
                """,
          )

      val compilation =
          KotlinCompilation().apply {
            sources = listOf(source)
            configureKsp { symbolProcessorProviders += mutableListOf(StegoProcessorProvider()) }
            inheritClassPath = true
          }

      When("the processor is run") {
        val result = compilation.compile()

        Then("the compilation should succeed") {
          result.exitCode shouldBe KotlinCompilation.ExitCode.OK
        }

        And("the generated JSON should contain the correct generic type metadata") {
          val generatedFile = File(compilation.kspSourcesDir, "resources/stego/components.json")
          val components = Json.decodeFromString<List<ComponentMetadata>>(generatedFile.readText())

          components.size shouldBe 1
          components.first().properties.any {
            it.name == "data" && it.typeQualifiedName == "kotlin.collections.List<kotlin.String>"
          } shouldBe true
        }
      }
    }

    Given("a source file with a StegoNode that inherits from a base component") {
      val source =
          SourceFile.kotlin(
              "InheritingNode.kt",
              """
                package stego.tests

                import uk.co.developmentanddinosaurs.stego.annotations.StegoNode

                open class BaseComponent(val baseProperty: String)

                @StegoNode("inheriting.node")
                data class InheritingNode(val ownProperty: String) : BaseComponent("test")
                """,
          )

      val compilation =
          KotlinCompilation().apply {
            sources = listOf(source)
            configureKsp { symbolProcessorProviders += mutableListOf(StegoProcessorProvider()) }
            inheritClassPath = true
          }

      When("the processor is run") {
        val result = compilation.compile()

        Then("the compilation should succeed") {
          result.exitCode shouldBe KotlinCompilation.ExitCode.OK
        }

        And("it should generate a base-components.json file") {
          val generatedFile =
              File(compilation.kspSourcesDir, "resources/stego/base-components.json")
          generatedFile.exists() shouldBe true

          val baseComponents =
              Json.decodeFromString<List<BaseComponentMetadata>>(generatedFile.readText())
          baseComponents shouldBe
              listOf(BaseComponentMetadata("stego.tests.BaseComponent", listOf("baseProperty")))
        }
      }
    }
  }
}
