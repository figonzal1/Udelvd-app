package cl.udelvd.refactor.project_feature.ui

import cl.udelvd.refactor.project_feature.domain.model.Project

data class ProjectState(
    val projectList: List<Project> = emptyList(),
    val isLoading: Boolean = true
)