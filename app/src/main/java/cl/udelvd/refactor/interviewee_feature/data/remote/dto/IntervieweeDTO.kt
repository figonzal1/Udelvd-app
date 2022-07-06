package cl.udelvd.refactor.interviewee_feature.data.remote.dto

import androidx.annotation.Keep
import cl.udelvd.refactor.interviewee_feature.domain.model.Interviewee
import com.google.gson.annotations.SerializedName

@Keep
data class IntervieweeDTO(

    val id: String,

    @SerializedName("nombre")
    val name: String,

    @SerializedName("apellido")
    val lastName: String,

    @SerializedName("n_eventos")
    val nEvents: Int
) {
    fun toDomain() = Interviewee(
        id = id.toInt(),
        name = name.trim(),
        lastName = lastName.trim(),
        nEvents = nEvents
    )
}