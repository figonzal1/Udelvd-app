package cl.udelvd.refactor.interviewee_feature.data.remote

import cl.udelvd.refactor.project_feature.domain.model.Project

class IntervieweeRemoteDataSource(
    private val intervieweeAPI: IntervieweeAPI
) {

    suspend fun getIntervieweeWithEvents(
        authToken: String,
        selectedProjects: List<Project>
    ) = when {
        selectedProjects.isNotEmpty() -> {
            val ids = processFilterProjects(selectedProjects)
            intervieweeAPI.getIntervieweeWithEventsByProject(authToken, ids)
        }
        else -> {
            intervieweeAPI.getIntervieweeWithEvents(authToken)
        }
    }.body()?.data


    //TODO: CONVERT IN GENERIC FUNCTION
    private fun processFilterProjects(filterProjects: List<Project>): String {
        var ids = ""
        for (i in filterProjects.indices) {

            when {
                i != filterProjects.size - 1 -> ids += "${filterProjects[i].id};"
                i == filterProjects.size - 1 -> ids += "${filterProjects[i].id}"
            }
        }

        return ids
    }
}