package cl.udelvd.refactor.interviewee_feature.domain.use_case

import cl.udelvd.refactor.interviewee_feature.domain.repository.IntervieweeRepository

class GetIntervieweeWithEventsUseCase(
    private val intervieweeRepository: IntervieweeRepository
) {
    operator fun invoke(authToken: String) =
        intervieweeRepository.getIntervieweeWithEvents(authToken)
}