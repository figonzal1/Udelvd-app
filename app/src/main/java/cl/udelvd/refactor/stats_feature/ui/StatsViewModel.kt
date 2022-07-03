package cl.udelvd.refactor.stats_feature.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.stats_feature.domain.use_case.GetStatsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class StatsViewModel(
    private val getStatsUseCase: GetStatsUseCase
) : ViewModel() {

    private val _statsState = MutableStateFlow(StatsState())
    val statsState = _statsState.asStateFlow()

    private val _errorState = Channel<String>()
    val errorState = _errorState.receiveAsFlow()


    fun getStats(authToken: String, idSelectedEmoticon: Int = -1) {

        viewModelScope.launch {

            getStatsUseCase(authToken, idSelectedEmoticon).collect {

                when (it) {
                    is StatusAPI.Loading -> {
                        _statsState.value = statsState.value.copy(
                            isLoading = true
                        )
                    }
                    is StatusAPI.Error -> {

                        _errorState.send("Error al cargar los stats")

                        _statsState.value = statsState.value.copy(
                            isLoading = false
                        )
                    }
                    is StatusAPI.Success -> {
                        _statsState.value = statsState.value.copy(
                            isLoading = false,
                            stats = it.data
                        )
                    }
                }
            }
        }
    }
}