package cl.udelvd.refactor.stats_feature.domain.use_case

import cl.udelvd.refactor.interviewee_feature.domain.model.Interviewee
import cl.udelvd.refactor.project_feature.domain.model.Project
import cl.udelvd.refactor.stats_feature.domain.repository.StatsRepository

class GetStatsUseCase(
    private val statsRepository: StatsRepository
) {
    operator fun invoke(
        authToken: String,
        idSelectedEmoticon: Int,
        genreLetter: String,
        selectedProjects: List<Project>,
        selectedInterviewees: List<Interviewee>
    ) =
        statsRepository.getStats(
            authToken,
            idSelectedEmoticon,
            genreLetter,
            selectedProjects,
            selectedInterviewees
        )
}