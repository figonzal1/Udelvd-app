package cl.udelvd.refactor.stats_feature.ui

import cl.udelvd.refactor.stats_feature.data.remote.AttributesResult

data class StatsState(
    val stats: AttributesResult? = null,
    val isLoading: Boolean = true
)