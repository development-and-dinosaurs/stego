package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.ui.ButtonView
import uk.co.developmentanddinosaurs.stego.ui.ColumnView
import uk.co.developmentanddinosaurs.stego.ui.LabelView
import uk.co.developmentanddinosaurs.stego.ui.View

interface ViewDto {
    fun toDomain(): View
}

@Serializable
data class ColumnViewDto(val children: List<ViewDto>) : ViewDto {
    override fun toDomain(): View = ColumnView(
        children = children.map { it.toDomain() }
    )
}

@Serializable
data class LabelViewDto(val text: String) : ViewDto {
    override fun toDomain(): View = LabelView(
        text = text
    )
}

@Serializable
data class ButtonViewDto(val text: String, val onClick: EventDto) : ViewDto {
    override fun toDomain(): View = ButtonView(
        text = text,
        onClick = onClick.toDomain()
    )
}
