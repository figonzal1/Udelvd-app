package cl.udelvd.refactor.interviewee_feature.domain.model

import cl.udelvd.refactor.core.domain.model.Domain

data class Interviewee(
    override val id: Int,
    val name: String,
    val lastName: String,
    val nEvents: Int
) : Domain