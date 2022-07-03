package cl.udelvd.refactor.stats_feature.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface StatsAPI {

    //@GET("/estadisticas/2")
    //suspend fun getStats(@Header("Authorization") authorization: String): Response<StatsResult>

    @GET("/estadisticas/")
    suspend fun getStats(): Response<StatsResult>

    @GET("/estadisticas/emoticon/{id_emoticon}/genero/{nombre}")
    suspend fun getStatsByEmoticonAndGenre(
        @Path("id_emoticon") idEmoticon: Int,
        @Path("nombre") genreLetter: String
    ): Response<StatsResult>

    @GET("/estadisticas/emoticon/{id_emoticon}")
    suspend fun getStatsByEmoticon(@Path("id_emoticon") idEmoticon: Int): Response<StatsResult>

    @GET("/estadisticas/genero/{nombre}")
    suspend fun getStatsByGenre(@Path("nombre") genreLetter: String): Response<StatsResult>
}