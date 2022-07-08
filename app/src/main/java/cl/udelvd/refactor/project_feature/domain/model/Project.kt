package cl.udelvd.refactor.project_feature.domain.model

import androidx.annotation.Keep
import cl.udelvd.refactor.core.domain.model.Domain

@Keep
data class Project(
    override val id: Int,
    val name: String
) : Domain
