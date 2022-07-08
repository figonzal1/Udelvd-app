package cl.udelvd.refactor.project_feature.data.repository

import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.project_feature.data.remote.ProjectRemoteDataSource
import cl.udelvd.refactor.project_feature.domain.model.Project
import cl.udelvd.refactor.project_feature.domain.repository.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class ProjectRepositoryImpl(
    private val projectRemoteDataSource: ProjectRemoteDataSource
) : ProjectRepository {
    override fun getProjects(authToken: String): Flow<StatusAPI<List<Project>>> = flow {

        emit(StatusAPI.Loading())

        try {

            val projects =
                projectRemoteDataSource.getProjects(authToken)?.map { it.attributes.toDomain() }

            when {
                !projects.isNullOrEmpty() -> emit(StatusAPI.Success(projects))
            }

        } catch (e: HttpException) {
            Timber.e(e.message())
        } catch (e: IOException) {
            Timber.e(e.message)
        }
    }.flowOn(Dispatchers.IO)
}