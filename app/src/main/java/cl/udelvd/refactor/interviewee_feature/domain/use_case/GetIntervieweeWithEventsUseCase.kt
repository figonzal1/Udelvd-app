package cl.udelvd.refactor.interviewee_feature.domain.use_case

import cl.udelvd.refactor.interviewee_feature.domain.repository.IntervieweeRepository
import cl.udelvd.refactor.project_feature.domain.model.Project

class GetIntervieweeWithEventsUseCase(
    private val intervieweeRepository: IntervieweeRepository
) {
    operator fun invoke(authToken: String, selectedProjects: MutableList<Project>) =
        intervieweeRepository.getIntervieweeWithEvents(authToken, selectedProjects)
}