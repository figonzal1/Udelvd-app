package cl.udelvd.refactor.emoticons_feature.ui

import cl.udelvd.refactor.emoticons_feature.domain.model.Emoticon

data class EmoticonState(
    val emoticonList: List<Emoticon> = emptyList(),
    val isLoading: Boolean = true
)
