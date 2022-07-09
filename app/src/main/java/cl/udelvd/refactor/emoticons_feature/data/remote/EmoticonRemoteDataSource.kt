package cl.udelvd.refactor.emoticons_feature.data.remote

class EmoticonRemoteDataSource(
    private val emoticonsAPI: EmoticonsAPI
) {

    suspend fun getEmoticons(autoToken: String, language: String) =
        emoticonsAPI.getEmoticonsByLanguage(autoToken, language).body()?.data
}