package cl.udelvd.refactor.stats_feature.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.interviewee_feature.domain.model.Interviewee
import cl.udelvd.refactor.interviewee_feature.domain.use_case.GetIntervieweeWithEventsUseCase
import cl.udelvd.refactor.interviewee_feature.ui.IntervieweeState
import cl.udelvd.refactor.stats_feature.domain.use_case.GetStatsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class StatsViewModel(
    private val getStatsUseCase: GetStatsUseCase,
    private val getIntervieweeWithEventsUseCase: GetIntervieweeWithEventsUseCase
) : ViewModel() {

    private val _statsState = MutableStateFlow(StatsState())
    val statsState = _statsState.asStateFlow()

    private val _intervieweeState = MutableStateFlow(IntervieweeState())
    val intervieweeState = _intervieweeState.asStateFlow()

    private val _errorState = Channel<String>()
    val errorState = _errorState.receiveAsFlow()


    fun getStats(authToken: String, idSelectedEmoticon: Int = -1, genreLetter: String = "") {

        viewModelScope.launch {

            getStatsUseCase(authToken, idSelectedEmoticon, genreLetter).collect {

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

    fun getIntervieweeWithEvents(authToken: String) {
        viewModelScope.launch {

            getIntervieweeWithEventsUseCase(authToken).collect {

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
}