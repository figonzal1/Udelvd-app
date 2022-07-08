package cl.udelvd.refactor.interviewee_feature.domain.repository

import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.interviewee_feature.domain.model.Interviewee
import cl.udelvd.refactor.project_feature.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface IntervieweeRepository {

    fun getIntervieweeWithEvents(
        authToken: String,
        selectedProjects: MutableList<Project>
    ): Flow<StatusAPI<List<Interviewee>>>
}