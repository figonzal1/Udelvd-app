package cl.udelvd.refactor.stats_feature.data.remote

import androidx.annotation.Keep
import cl.udelvd.refactor.stats_feature.data.remote.dto.GeneralStatsDTO
import cl.udelvd.refactor.stats_feature.data.remote.dto.IntervieweeGenreDTO
import com.google.gson.annotations.SerializedName

@Keep
data class StatsResult(
    @SerializedName("links") var links: Any,
    val data: List<DataResult>
)

@Keep
data class DataResult(
    val type: String,
    val attributes: AttributesResult
)

@Keep
data class AttributesResult(
    val general: GeneralStatsDTO,

    @SerializedName("entrevistados_por_genero")
    val intervieweeByGenre: IntervieweeGenreDTO
)

