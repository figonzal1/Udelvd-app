package cl.udelvd.refactor.emoticons_feature.domain.model

import androidx.annotation.Keep

@Keep
data class Emoticon(
    val id: Int,
    val url: String,
    val description: String
)
