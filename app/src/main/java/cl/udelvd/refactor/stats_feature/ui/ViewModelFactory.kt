package cl.udelvd.refactor.stats_feature.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.udelvd.ApplicationController
import cl.udelvd.refactor.interviewee_feature.data.remote.IntervieweeRemoteDataSource
import cl.udelvd.refactor.interviewee_feature.data.repository.IntervieweeRepositoryImpl
import cl.udelvd.refactor.interviewee_feature.domain.use_case.GetIntervieweeWithEventsUseCase
import cl.udelvd.refactor.project_feature.data.remote.ProjectRemoteDataSource
import cl.udelvd.refactor.project_feature.data.repository.ProjectRepositoryImpl
import cl.udelvd.refactor.project_feature.domain.use_case.GetProjectUseCase
import cl.udelvd.refactor.stats_feature.data.remote.StatsRemoteDataSource
import cl.udelvd.refactor.stats_feature.data.repository.StatsRepositoryImpl
import cl.udelvd.refactor.stats_feature.domain.use_case.GetStatsUseCase

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val app = (application as ApplicationController)

        val statsApi = app.statsApi
        val statsRemoteDataSource = StatsRemoteDataSource(statsApi)

        val intervieweeApi = app.intervieweesApi
        val intervieweeRemoteDataSource = IntervieweeRemoteDataSource(intervieweeApi)

        val projectApi = app.projectApi
        val projectRemoteDataSource = ProjectRemoteDataSource(projectApi)

        when {
            modelClass.isAssignableFrom(StatsViewModel::class.java) -> {
                return StatsViewModel(
                    GetStatsUseCase(StatsRepositoryImpl(statsRemoteDataSource)),
                    GetIntervieweeWithEventsUseCase(
                        IntervieweeRepositoryImpl(intervieweeRemoteDataSource)
                    ),
                    GetProjectUseCase(ProjectRepositoryImpl(projectRemoteDataSource))
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
