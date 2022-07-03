package cl.udelvd.refactor.stats_feature.data.remote

class StatsRemoteDataSource(
    private val statsAPI: StatsAPI
) {

    suspend fun getStats(
        authToken: String,
        idSelectedEmoticon: Int,
        genreLetter: String
    ): AttributesResult? = when {

        idSelectedEmoticon != -1 && genreLetter != "" -> {
            statsAPI.getStatsByEmoticonAndGenre(idSelectedEmoticon, genreLetter)
        }
        idSelectedEmoticon != -1 -> statsAPI.getStatsByEmoticon(idSelectedEmoticon)
        genreLetter != "" -> statsAPI.getStatsByGenre(genreLetter)
        else -> statsAPI.getStats()

    }.body()?.data?.first()?.attributes
}