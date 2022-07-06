package cl.udelvd.refactor.stats_feature.data.repository

import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.interviewee_feature.domain.model.Interviewee
import cl.udelvd.refactor.stats_feature.data.remote.StatsAttributesResult
import cl.udelvd.refactor.stats_feature.data.remote.StatsRemoteDataSource
import cl.udelvd.refactor.stats_feature.domain.repository.StatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class StatsRepositoryImpl(
    private val statsRemoteDataSource: StatsRemoteDataSource
) : StatsRepository {

    override fun getStats(
        authToken: String,
        idSelectedEmoticon: Int,
        genreLetter: String,
        filterInterviewees: List<Interviewee>
    ): Flow<StatusAPI<StatsAttributesResult>> = flow {

        emit(StatusAPI.Loading())

        try {

            val stats = statsRemoteDataSource.getStats(
                authToken,
                idSelectedEmoticon,
                genreLetter,
                filterInterviewees
            )

            stats?.let {
                emit(StatusAPI.Success(it))
            }

        } catch (e: HttpException) {
            Timber.e(e.message())
        } catch (e: IOException) {
            Timber.e(e.message)
        }
    }.flowOn(Dispatchers.IO)
}