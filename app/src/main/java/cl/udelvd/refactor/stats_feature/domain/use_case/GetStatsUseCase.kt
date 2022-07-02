package cl.udelvd.refactor.stats_feature.domain.use_case

import cl.udelvd.refactor.stats_feature.domain.repository.StatsRepository

class GetStatsUseCase(
    private val statsRepository: StatsRepository
) {
    operator fun invoke(authToken: String) = statsRepository.getStats(authToken)
}