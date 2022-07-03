package cl.udelvd.refactor.stats_feature.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface StatsAPI {

    //@GET("/estadisticas/2")
    //suspend fun getStats(@Header("Authorization") authorization: String): Response<StatsResult>

    @GET("/estadisticas/")
    suspend fun getStats(): Response<StatsResult>

    @GET("/estadisticas/emoticon/{id_emoticon}")
    suspend fun getStats(@Path("id_emoticon") idEmoticon: Int): Response<StatsResult>
}