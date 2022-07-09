package cl.udelvd.refactor.emoticons_feature.data.remote

import cl.udelvd.refactor.core.data.remote.RootResult
import cl.udelvd.refactor.core.data.remote.dto.GenericDataResult
import cl.udelvd.refactor.emoticons_feature.data.remote.dto.EmoticonDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface EmoticonsAPI {

    @GET("/emoticones/idioma/{idioma}")
    suspend fun getEmoticonsByLanguage(
        @Header("Authorization") authToken: String,
        @Path("idioma") idioma: String
    ): Response<RootResult<GenericDataResult<EmoticonDTO>>>
}