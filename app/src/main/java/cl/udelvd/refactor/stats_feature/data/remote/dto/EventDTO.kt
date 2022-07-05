package cl.udelvd.refactor.stats_feature.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class EventDTO(
    val nombre: String,
    val apellido: String,
    val accion: String,

    @SerializedName("hora_evento")
    val horaEvento: String,

    val justificacion: String,

    @SerializedName("url")
    val emoticonUrl: String
)
