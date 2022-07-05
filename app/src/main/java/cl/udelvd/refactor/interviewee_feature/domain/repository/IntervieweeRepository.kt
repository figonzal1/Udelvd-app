package cl.udelvd.refactor.interviewee_feature.domain.repository

import cl.udelvd.refactor.StatusAPI
import cl.udelvd.refactor.interviewee_feature.domain.model.Interviewee
import kotlinx.coroutines.flow.Flow

interface IntervieweeRepository {

    fun getIntervieweeWithEvents(authToken: String): Flow<StatusAPI<List<Interviewee>>>
}