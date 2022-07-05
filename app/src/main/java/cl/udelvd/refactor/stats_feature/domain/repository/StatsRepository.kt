package cl.udelvd.refactor.stats_feature.domain.repository

import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.stats_feature.data.remote.StatsAttributesResult
import kotlinx.coroutines.flow.Flow

interface StatsRepository {

    fun getStats(
        authToken: String,
        idSelectedEmoticon: Int,
        genreLetter: String
    ): Flow<StatusAPI<StatsAttributesResult>>
}