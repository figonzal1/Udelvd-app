package cl.udelvd.refactor.stats_feature.domain.repository

import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.stats_feature.data.remote.DataResult
import kotlinx.coroutines.flow.Flow

interface StatsRepository {

    fun getStats(authToken: String): Flow<StatusAPI<List<DataResult>>>
}