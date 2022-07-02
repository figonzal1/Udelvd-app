package cl.udelvd.refactor.stats_feature.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.udelvd.ApplicationController
import cl.udelvd.refactor.stats_feature.data.remote.StatsRemoteDataSource
import cl.udelvd.refactor.stats_feature.data.repository.StatsRepositoryImpl
import cl.udelvd.refactor.stats_feature.domain.use_case.GetStatsUseCase

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val statsApi = (application as ApplicationController).statsApi
        val statsRemoteDataSource = StatsRemoteDataSource(statsApi)

        when {
            modelClass.isAssignableFrom(StatsViewModel::class.java) -> {
                return StatsViewModel(
                    GetStatsUseCase(StatsRepositoryImpl(statsRemoteDataSource))
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}