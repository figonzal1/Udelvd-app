package cl.udelvd.refactor.interviewee_feature.data.remote

import cl.udelvd.refactor.core.data.remote.RootResult
import cl.udelvd.refactor.core.data.remote.dto.GenericDataResult
import cl.udelvd.refactor.interviewee_feature.data.remote.dto.IntervieweeDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface IntervieweeAPI {

    @GET("/entrevistados/eventos")
    suspend fun getIntervieweeWithEvents(
        @Header("Authorization") authToken: String
    ): Response<RootResult<GenericDataResult<IntervieweeDTO>>>

    @GET("/entrevistados/eventos/proyectos/{ids}")
    suspend fun getIntervieweeWithEventsByProject(
        @Header("Authorization") authToken: String,
        @Path(value = "ids", encoded = true) ids: String
    ): Response<RootResult<GenericDataResult<IntervieweeDTO>>>
}