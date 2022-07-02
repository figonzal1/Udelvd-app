package cl.udelvd.refactor.stats_feature.ui

import cl.udelvd.refactor.stats_feature.data.remote.DataResult

data class StatsState(
    val stats: List<DataResult> = emptyList(),
    val isLoading: Boolean = true
)