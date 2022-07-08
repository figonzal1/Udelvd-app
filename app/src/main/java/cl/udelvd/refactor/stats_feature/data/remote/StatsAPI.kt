package cl.udelvd.refactor.stats_feature.data.remote

import cl.udelvd.refactor.core.data.remote.RootResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface StatsAPI {

    @GET("/estadisticas/")
    suspend fun getStats(): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/emoticon/{id_emoticon}/genero/{nombre}/entrevistados/{ids}")
    suspend fun getStatsByEmoticonAndGenreAndInterviewees(
        @Path("id_emoticon") idEmoticon: Int,
        @Path("nombre") genreLetter: String,
        @Path(value = "ids", encoded = true) ids: String
    ): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/emoticon/{id_emoticon}/genero/{nombre}")
    suspend fun getStatsByEmoticonAndGenre(
        @Path("id_emoticon") idEmoticon: Int,
        @Path("nombre") genreLetter: String
    ): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/emoticon/{id_emoticon}/entrevistados/{ids}")
    suspend fun getStatsByEmoticonAndInterviewees(
        @Path("id_emoticon") idEmoticon: Int,
        @Path(value = "ids", encoded = true) ids: String
    ): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/genero/{nombre}/entrevistados/{ids}")
    suspend fun getStatsByGenreAndInterviewees(
        @Path("nombre") genreLetter: String,
        @Path(value = "ids", encoded = true) ids: String
    ): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/emoticon/{id_emoticon}")
    suspend fun getStatsByEmoticon(
        @Path("id_emoticon") idEmoticon: Int
    ): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/genero/{nombre}")
    suspend fun getStatsByGenre(
        @Path("nombre") genreLetter: String
    ): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/entrevistados/{ids}")
    suspend fun getStatsByInterviewees(
        @Path(value = "ids", encoded = true) ids: String
    ): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/proyectos/{ids}")
    suspend fun getStatsByProjects(
        @Path(value = "ids", encoded = true) ids: String
    ): Response<RootResult<StatsDataResult>>

}