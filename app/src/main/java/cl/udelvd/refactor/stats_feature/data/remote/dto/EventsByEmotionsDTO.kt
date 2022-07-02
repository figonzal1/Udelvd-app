package cl.udelvd.refactor.stats_feature.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class EventsByEmotionsDTO(

    @SerializedName("felicidad")
    val happy: Int,

    @SerializedName("tristeza")
    val sad: Int,

    @SerializedName("miedo")
    val fear: Int,

    @SerializedName("enojo")
    val angry: Int,
)
