package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

interface StateDto {
    val id: String
    val initial: String?
    val invoke: InvokableDefinitionDto?
    val on: Map<String, List<TransitionDto>>
    val onEntry: List<ActionDto>
    val onExit: List<ActionDto>
    val states: Map<String, StateDto>
}
