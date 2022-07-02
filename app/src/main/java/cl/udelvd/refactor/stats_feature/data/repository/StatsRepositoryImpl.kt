package cl.udelvd.refactor.stats_feature.data.repository

import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.stats_feature.data.remote.DataResult
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

    override fun getStats(authToken: String): Flow<StatusAPI<List<DataResult>>> = flow {

        emit(StatusAPI.Loading())

        try {
            emit(StatusAPI.Success(statsRemoteDataSource.getStats(authToken)))

        } catch (e: HttpException) {
            Timber.e(e.message())
        } catch (e: IOException) {
            Timber.e(e.message)
        }
    }.flowOn(Dispatchers.IO)
}