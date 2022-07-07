package cl.udelvd.refactor.project_feature.data.remote

import cl.udelvd.refactor.core.data.remote.dto.GenericDataResult
import cl.udelvd.refactor.project_feature.data.remote.dto.ProjectDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ProjectAPI {

    @GET("/proyectos")
    suspend fun getProjects(
        @Header("Authorization") authToken: String
    ): Response<GenericDataResult<List<ProjectDTO>>>
}