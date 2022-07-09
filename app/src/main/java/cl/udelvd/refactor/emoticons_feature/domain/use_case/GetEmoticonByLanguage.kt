package cl.udelvd.refactor.emoticons_feature.domain.use_case

import cl.udelvd.refactor.emoticons_feature.domain.repository.EmoticonRepository

class GetEmoticonByLanguage(
    private val emoticonRepository: EmoticonRepository

) {
    operator fun invoke(authToken: String, language: String) =
        emoticonRepository.getEmoticons(authToken, language)
}