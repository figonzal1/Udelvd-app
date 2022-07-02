package cl.udelvd.refactor.stats_feature.data.remote

class StatsRemoteDataSource(
    private val statsAPI: StatsAPI
) {

    suspend fun getStats(authToken: String): AttributesResult? {

        val call = statsAPI.getStats()
        return call.body()?.data?.first()?.attributes
    }
}