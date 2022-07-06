package cl.udelvd.refactor.interviewee_feature.domain.model

data class Interviewee(
    val id: Int,
    val name: String,
    val lastName: String,
    val nEvents: Int
)