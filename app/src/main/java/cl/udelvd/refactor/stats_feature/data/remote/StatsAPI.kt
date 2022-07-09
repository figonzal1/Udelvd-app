package cl.udelvd.refactor.stats_feature.data.remote

import cl.udelvd.refactor.core.data.remote.RootResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface StatsAPI {

    @GET("/estadisticas/")
    suspend fun getStats(): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/emoticon/{id_emoticon}/genero/{nombre}/entrevistados/{ids_entrevistados}/proyectos/{ids_proyectos}")
    suspend fun getStatsByEmoticonAndGenreAndIntervieweesAndProjects(
        @Path("id_emoticon") idSelectedEmoticon: Int,
        @Path("nombre") genreLetter: String,
        @Path(value = "ids_proyectos") projectsIds: String,
        @Path(value = "ids_entrevistados") intervieweeIds: String
    ): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/emoticon/{id_emoticon}/genero/{nombre}/proyectos/{ids_proyectos}")
    suspend fun getStatsByEmoticonAndGenreAndProjects(
        @Path("id_emoticon") idSelectedEmoticon: Int,
        @Path("nombre") genreLetter: String,
        @Path(value = "ids_proyectos") projectsIds: String
    ): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/emoticon/{id_emoticon}/entrevistados/{ids_entrevistados}/proyectos/{ids_proyectos}")
    suspend fun getStatsByEmoticonAndIntervieweesAndProjects(
        @Path("id_emoticon") idSelectedEmoticon: Int,
        @Path(value = "ids_proyectos") projectsIds: String,
        @Path(value = "ids_entrevistados") intervieweeIds: String
    ): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/genero/{nombre}/entrevistados/{ids_entrevistados}/proyectos/{ids_proyectos}")
    suspend fun getStatsByGenreAndIntervieweesAndProjects(
        @Path("nombre") genreLetter: String,
        @Path(value = "ids_proyectos") projectsIds: String,
        @Path(value = "ids_entrevistados") intervieweeIds: String
    ): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/emoticon/{id_emoticon}/proyectos/{ids_proyectos}")
    suspend fun getStatsByEmoticonAndProjects(
        @Path("id_emoticon") idSelectedEmoticon: Int,
        @Path(value = "ids_proyectos") projectsIds: String
    ): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/genero/{nombre}/proyectos/{ids_proyectos}")
    suspend fun getStatsByGenreAndProjects(
        @Path("nombre") genreLetter: String,
        @Path(value = "ids_proyectos") projectsIds: String
    ): Response<RootResult<StatsDataResult>>

    @GET("/estadisticas/entrevistados/{ids_entrevistados}/proyectos/{ids_proyectos}")
    suspend fun getStatsByIntervieweesAndProjects(
        @Path(value = "ids_proyectos") projectIds: String,
        @Path(value = "ids_entrevistados") intervieweeIds: String
    ): Response<RootResult<StatsDataResult>>

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