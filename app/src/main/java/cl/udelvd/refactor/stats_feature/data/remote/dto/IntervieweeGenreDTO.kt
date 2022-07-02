package cl.udelvd.refactor.stats_feature.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class IntervieweeGenreDTO(

    @SerializedName("total_femenino")
    val totalWomen: Int,

    @SerializedName("total_masculino")
    val totalMen: Int,

    @SerializedName("total_otros")
    val totalOther: Int
)
