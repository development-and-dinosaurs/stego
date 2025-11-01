package uk.co.developmentanddinosaurs.stego.processor

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import com.tschuchort.compiletesting.kspSourcesDir
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.File

@OptIn(ExperimentalCompilerApi::class)
class StegoProcessorTest : BehaviorSpec({
    Given("a source file with a class annotated with @StegoNode") {
        val source =
            SourceFile.kotlin(
                "MyTestNode.kt",
                """
                package stego.tests
                
                import uk.co.developmentanddinosaurs.stego.annotations.StegoNode
                @StegoNode("test.node")
                data class MyTestNode(val id: String)
                """
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

            And("it should generate a text file") {
                val generatedFile = File(compilation.kspSourcesDir, "resources/stego_annotated_classes.txt")
                generatedFile.exists() shouldBe true
            }

            And("the file should contain the class's qualified name") {
                val generatedFile = File(compilation.kspSourcesDir, "resources/stego_annotated_classes.txt")
                generatedFile.readLines().first() shouldBe "stego.tests.MyTestNode"
            }
        }
    }
})
