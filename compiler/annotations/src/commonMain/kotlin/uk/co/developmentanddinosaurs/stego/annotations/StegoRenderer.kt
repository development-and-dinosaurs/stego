package uk.co.developmentanddinosaurs.stego.annotations

/**
 * Annotates a Composable function that renders a specific UiNode. The first parameter of the
 * function MUST be the UiNode it renders.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class StegoRenderer
