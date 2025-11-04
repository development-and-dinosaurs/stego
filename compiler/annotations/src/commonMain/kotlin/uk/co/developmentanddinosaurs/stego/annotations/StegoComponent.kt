package uk.co.developmentanddinosaurs.stego.annotations

/**
 * Annotates a sealed class or interface that represents a fundamental, interchangeable component
 * within the Stego framework.
 *
 * This signals to the Stego processor that its subclasses should be discovered for generation tasks.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class StegoComponent(
    val type: String = "",
)
