package cl.udelvd.refactor.stats_feature.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.interviewee_feature.domain.model.Interviewee
import cl.udelvd.refactor.interviewee_feature.domain.use_case.GetIntervieweeWithEventsUseCase
import cl.udelvd.refactor.interviewee_feature.ui.IntervieweeState
import cl.udelvd.refactor.project_feature.domain.model.Project
import cl.udelvd.refactor.project_feature.domain.use_case.GetProjectUseCase
import cl.udelvd.refactor.project_feature.ui.ProjectState
import cl.udelvd.refactor.stats_feature.domain.use_case.GetStatsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class StatsViewModel(
    private val getStatsUseCase: GetStatsUseCase,
    private val getIntervieweeWithEventsUseCase: GetIntervieweeWithEventsUseCase,
    private val getProjectUseCase: GetProjectUseCase
) : ViewModel() {

    private val _statsState = MutableStateFlow(StatsState())
    val statsState = _statsState.asStateFlow()

    private val _intervieweeState = MutableStateFlow(IntervieweeState())
    val intervieweeState = _intervieweeState.asStateFlow()

    private val _projectState = MutableStateFlow(ProjectState())
    val projectState = _projectState.asStateFlow()

    private val _errorState = Channel<String>()
    val errorState = _errorState.receiveAsFlow()


    fun getStats(
        authToken: String,
        idSelectedEmoticon: Int = -1,
        genreLetter: String = "",
        selectedProjects: List<Project>,
        selectedInterviewees: List<Interviewee>
    ) {

        viewModelScope.launch {

            getStatsUseCase(
                authToken,
                idSelectedEmoticon,
                genreLetter,
                selectedProjects,
                selectedInterviewees
            ).collect {

                when (it) {
                    is StatusAPI.Loading -> {
                        _statsState.value = statsState.value.copy(
                            isLoading = true
                        )
                    }
                    is StatusAPI.Success -> {
                        _statsState.value = statsState.value.copy(
                            isLoading = false,
                            stats = it.data
                        )
                    }
                    is StatusAPI.Error -> {

                        _errorState.send("Error al cargar los stats")

                        _statsState.value = statsState.value.copy(
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun getIntervieweeWithEvents(authToken: String, selectedProjects: MutableList<Project>) {
        viewModelScope.launch {

            getIntervieweeWithEventsUseCase(authToken, selectedProjects).collect {

                when (it) {

                    is StatusAPI.Loading -> {
                        _intervieweeState.value = intervieweeState.value.copy(
                            isLoading = true
                        )
                    }
                    is StatusAPI.Success -> {
                        _intervieweeState.value = intervieweeState.value.copy(
                            isLoading = false,
                            interviewee = it.data as List<Interviewee>
                        )
                    }
                    is StatusAPI.Error -> {
                        _errorState.send("Error al cargar los entrevistados")

                        _intervieweeState.value = intervieweeState.value.copy(
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun getProjects(authToken: String) {
        viewModelScope.launch {

            getProjectUseCase(authToken).collect {

                when (it) {
                    is StatusAPI.Error -> {
                        _errorState.send("Error al cargar proyectos")

                        _projectState.value = _projectState.value.copy(isLoading = false)
                    }
                    is StatusAPI.Loading -> {
                        _projectState.value = _projectState.value.copy(isLoading = true)
                    }
                    is StatusAPI.Success -> {
                        _projectState.value = projectState.value.copy(
                            isLoading = false,
                            projectList = it.data as List<Project>
                        )
                    }
                }
            }
        }
    }
}