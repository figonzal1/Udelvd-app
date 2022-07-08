package cl.udelvd.refactor.stats_feature.domain.repository

import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.interviewee_feature.domain.model.Interviewee
import cl.udelvd.refactor.project_feature.domain.model.Project
import cl.udelvd.refactor.stats_feature.data.remote.StatsAttributesResult
import kotlinx.coroutines.flow.Flow

interface StatsRepository {

    fun getStats(
        authToken: String,
        idSelectedEmoticon: Int,
        genreLetter: String,
        selectedProjects: List<Project>,
        selectedInterviewees: List<Interviewee>
    ): Flow<StatusAPI<StatsAttributesResult>>
}