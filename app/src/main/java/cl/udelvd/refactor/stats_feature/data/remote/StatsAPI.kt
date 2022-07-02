package cl.udelvd.refactor.stats_feature.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface StatsAPI {

    @GET("/estadisticas/2")
    suspend fun getStats(@Header("Authorization") authorization: String): Response<StatsResult>

    @GET("/estadisticas/")
    suspend fun getStats(): Response<StatsResult>
}