package cl.udelvd.refactor.stats_feature.data.remote

class StatsRemoteDataSource(
    private val statsAPI: StatsAPI
) {

    suspend fun getStats(authToken: String, idSelectedEmoticon: Int): AttributesResult? {

        val call = when {
            idSelectedEmoticon != -1 -> statsAPI.getStats(idSelectedEmoticon)
            else -> statsAPI.getStats()
        }

        return call.body()?.data?.first()?.attributes
    }
}