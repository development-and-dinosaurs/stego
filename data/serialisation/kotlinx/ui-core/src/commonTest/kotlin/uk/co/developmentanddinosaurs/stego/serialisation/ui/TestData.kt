package uk.co.developmentanddinosaurs.stego.serialisation.ui

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode

/** A test-specific UI Node for verifying UI node mapping, particularly for composition. */
internal object OtherUiNode : UiNode {
  override val id: String = "child-id"
}

/** A dummy DTO used for failure case testing in mappers. */
internal object OtherUiNodeDto : UiNodeDto {
  override val id: String = "other"
}

internal object UnknownUiNodeDto : UiNodeDto {
  override val id: String = "unknown"
}
