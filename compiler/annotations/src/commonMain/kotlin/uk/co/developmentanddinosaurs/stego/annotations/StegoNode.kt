package uk.co.developmentanddinosaurs.stego.annotations

/**
 * Annotates a UiNode data class to generate its DTO,
 * Mapper, and register it.
 * @param type The string identifier for JSON (e.g., "my-new").
 */
@StegoComponent
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class StegoNode(
    val type: String,
)
