package cl.udelvd.refactor.stats_feature.ui

import cl.udelvd.refactor.stats_feature.data.remote.StatsAttributesResult

data class StatsState(
    val stats: StatsAttributesResult? = null,
    val isLoading: Boolean = true
)