package cl.udelvd.refactor.project_feature.domain.use_case

import cl.udelvd.refactor.project_feature.domain.repository.ProjectRepository

class GetProjectUseCase(
    private val projectRepository: ProjectRepository
) {
    operator fun invoke(authToken: String) = projectRepository.getProjects(authToken)
}