package cl.udelvd.refactor.stats_feature.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class EventDTO(

    @SerializedName("nombre")
    val name: String,

    @SerializedName("apellido")
    val lastName: String,

    @SerializedName("accion")
    val action: String,

    @SerializedName("hora_evento")
    val eventHour: String,

    @SerializedName("justificacion")
    val justification: String,

    @SerializedName("url")
    val emoticonUrl: String
)
