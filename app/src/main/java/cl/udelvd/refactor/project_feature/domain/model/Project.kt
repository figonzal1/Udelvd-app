package cl.udelvd.refactor.project_feature.domain.model

import androidx.annotation.Keep

@Keep
data class Project(
    val id: Int,
    val name: String
)
