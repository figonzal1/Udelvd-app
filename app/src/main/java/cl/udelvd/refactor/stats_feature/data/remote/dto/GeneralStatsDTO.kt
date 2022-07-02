package cl.udelvd.refactor.stats_feature.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GeneralStatsDTO(
    @SerializedName("n_entrevistados")
    val nInterviewees: Int,

    @SerializedName("n_eventos")
    val nEvents: Int
)
