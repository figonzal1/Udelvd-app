package cl.udelvd.refactor.interviewee_feature.data.remote

import cl.udelvd.refactor.core.data.remote.dto.GenericDataResult
import cl.udelvd.refactor.interviewee_feature.data.remote.dto.IntervieweeDTO

class IntervieweeRemoteDataSource(
    private val intervieweeAPI: IntervieweeAPI
) {

    suspend fun getIntervieweeWithEvents(authToken: String): List<GenericDataResult<IntervieweeDTO>>? {

        return intervieweeAPI.getIntervieweeWithEvents(authToken).body()?.data
    }
}