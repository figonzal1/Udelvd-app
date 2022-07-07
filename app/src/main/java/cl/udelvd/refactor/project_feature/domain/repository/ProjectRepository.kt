package cl.udelvd.refactor.project_feature.domain.repository

import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.project_feature.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {

    fun getProjects(authToken: String): Flow<StatusAPI<List<Project>>>
}