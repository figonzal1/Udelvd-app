package cl.udelvd.refactor.emoticons_feature.domain.repository

import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.emoticons_feature.domain.model.Emoticon
import kotlinx.coroutines.flow.Flow

interface EmoticonRepository {

    fun getEmoticons(authToken: String, language: String): Flow<StatusAPI<List<Emoticon>>>
}