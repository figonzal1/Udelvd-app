package cl.udelvd.refactor.interviewee_feature.data.repository

import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.interviewee_feature.data.remote.IntervieweeRemoteDataSource
import cl.udelvd.refactor.interviewee_feature.domain.model.Interviewee
import cl.udelvd.refactor.interviewee_feature.domain.repository.IntervieweeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class IntervieweeRepositoryImpl(
    private val remoteDataSource: IntervieweeRemoteDataSource
) : IntervieweeRepository {

    override fun getIntervieweeWithEvents(authToken: String): Flow<StatusAPI<List<Interviewee>>> =
        flow {

            emit(StatusAPI.Loading())

            try {

                val interviewees = remoteDataSource.getIntervieweeWithEvents(authToken)?.map {
                    it.attributes.toDomain()
                }

                when {
                    !interviewees.isNullOrEmpty() -> emit(StatusAPI.Success(interviewees))
                }
            } catch (e: HttpException) {
                Timber.e(e.message())
            } catch (e: IOException) {
                Timber.e(e.message)
            }
        }.flowOn(Dispatchers.IO)
}