package cl.udelvd.refactor.emoticons_feature.data.remote.dto

import androidx.annotation.Keep
import cl.udelvd.refactor.emoticons_feature.domain.model.Emoticon
import com.google.gson.annotations.SerializedName

@Keep
data class EmoticonDTO(

    @SerializedName("id")
    val id: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("descripcion")
    val description: String
) {
    fun toDomain() = Emoticon(
        id = id.toInt(),
        url = url,
        description = description
    )
}
