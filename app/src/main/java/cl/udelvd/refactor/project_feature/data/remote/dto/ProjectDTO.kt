package cl.udelvd.refactor.project_feature.data.remote.dto

import androidx.annotation.Keep
import cl.udelvd.refactor.project_feature.domain.model.Project
import com.google.gson.annotations.SerializedName

@Keep
data class ProjectDTO(

    val id: String,
    @SerializedName("nombre")
    val name: String
) {
    fun toDomain() = Project(
        id = id.toInt(),
        name = name
    )
}