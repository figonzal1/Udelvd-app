package cl.udelvd.refactor.core.data.remote

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RootResult<T>(
    @SerializedName("links")
    var links: Any,

    val data: List<T> //GenericDataResult or StatsDataResult
)