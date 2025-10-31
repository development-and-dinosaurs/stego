# Adding a new UI node

Adding a new Stego UI node is accomplished by performing the following actions:

## Domain Layer
Add the new `UiNode` interface to the `domain:ui-core` module.

``` kotlin title="MyNewUiNode.kt"
data class MyNewUiNode(
    override val id: String,
    val text: String,
) : UiNode
```

## Presentation Layer
You should add the specific UI implementations here for each platform. The presentation layer in is `presentation:ui`.

### Add Android Implementation
Android implementations are in the `presentation:ui:android` module.

#### Add a new `Composable` implementation for the new node.

``` kotlin title="MyNewUiNode.kt"
@Composable
fun RenderMyNewUiNode(myNewUiNode: MyNewUiNode) {
    Text(text = myNewUiNode.text)
}
```

#### Add a new render function in `Render.kt`.
Need to update the `RenderInternal` function too, to add the branch to the `when` block.

``` kotlin title="Render.kt"
@Composable
private fun RenderMyNew(
    uiNode: MyNewUiNode,
    context: Context,
) {
    val resolvedNode = uiNode.copy(text = resolve(uiNode.text, context))
    RenderMyNewUiNode(resolvedNode)
}

@Composable
private fun RenderInternal(
    // Other parameters
    context: Context,
) {
    when (uiNode) {
        // Other nodes
        is MyNewUiNode -> RenderMyNew(uiNode, context)
    }
}
```

## Data Layer
The data layer needs updating to convert between JSON representations and the core domain models.

### Add Kotlinx Implementation
Kotlinx implementations are in the `data:serialisation:kotlinx:ui` module.

#### Add a new DTO for the JSON deserialisation.

``` kotlin title="MyNewUiNodeDto.kt"
@Serializable
@SerialName("my-new")
data class MyNewUiNodeDto(
    override val id: String,
    val text: String,
) : UiNodeDto
```

#### Add a new mapper between the DTO and the domain model.

``` kotlin title="MyNewUiNodeMapper.kt"
class MyNewUiNodeMapper : UiNodeMapper {
    override fun map(dto: UiNodeDto): UiNode {
        require(dto is MyNewUiNodeDto) { "MyNewUiNodeMapper can only map MyNewUiNodeDto" }
        return MyNewUiNode(
            id = dto.id,
            text = dto.text,
        )
    }
}
```

#### Add a test class and tests for the new mapper.
We use [Kotest](https://kotest.io/), specifically using the [BehaviourSpec](https://kotest.io/docs/framework/testing-styles.html#behavior-spec) because it's cool.

``` kotlin title="MyNewUiNodeMapperTest.kt"
class MyNewUiNodeMapperTest : BehaviorSpec({
    Given("a MyNewUiNodeMapper") {
        val mapper = MyNewUiNodeMapper()

        And("a MyNewUiNodeDto") {
            val dto = MyNewUiNodeDto(
                id = "my-id",
                text = "Hello, Stego!",
            )

            When("the dto is mapped") {
                val uiNode = mapper.map(dto)

                Then("all properties are mapped correctly") {
                    uiNode.shouldBeInstanceOf<MyNewUiNode>()
                    uiNode.id shouldBe "my-id"
                    uiNode.text shouldBe "Hello, Stego!"
                }
            }
        }

        And("a non-MyNewUiNodeDto") {
            val dto = OtherUiNodeDto

            When("the dto is mapped") {
                val exception = shouldThrow<IllegalArgumentException> {
                    mapper.map(dto)
                }
                
                Then("it should throw an IllegalArgumentException") {    
                    exception.message shouldBe "MyNewUiNodeMapper can only map MyNewUiNodeDto."
                }
            }
        }
    }
})
```

#### Add polymorphic subclass to StegoUiSerializersModule

``` kotlin title="StegoUiSerializersModule.kt"
val stegoUiSerializersModule = SerializersModule {
    polymorphic(UiNodeDto::class) {
        // Other subclasses
        subclass(ColumnUiNodeDto::class)
    }
    // Other polymorphic mappings
}
```

#### Add a test for the new UI node JSON

``` kotlin title="StegoUiSerializersModuleTest.kt"
class StegoUiSerializersModuleTest : BehaviorSpec({
    // Other tests
    Given("A JSON representation of MyNewUiNode") {
        val nodeJson =
            """
            {
              "type": "my-new",
              "id": "my-id",
              "text": "Welcome to Stego!"
            }
            """.trimIndent()
        When("deserializing a MyNewUiNodeDto") {
            val nodeDto = json.decodeFromString<UiNodeDto>(nodeJson)
            
            Then("it should deserialize correctly") {
                nodeDto.shouldBeInstanceOf<MyNewUiNodeDto>()
                nodeDto.id shouldBe "my-id"
                nodeDto.text shouldBe "Welcome to Stego!"
            }
        }
    }
```

## Dependency Injection Layer
The dependency injection layer is in `di`

### Add Koin Kotlinx injections
Kotlinx implementations are in the `di:koin:kotlinx:ui` module.

#### Add the mapper to the `StegoUiKoinModule`

``` kotlin title="StegoUiKoinModule.kt"
val module: Module = module {
    // Other stuff
    single {
        CompositeUiNodeMapper(
            simpleMappers =
                mapOf(
                    // Other pairs
                    MyNewUiNodeDto::class to MyNewUiNodeMapper(),
                )
        )
    } bind UiNodeMapper::class     
```
