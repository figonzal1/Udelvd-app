package cl.udelvd.refactor.project_feature.data.remote

class ProjectRemoteDataSource(
    private val projectAPI: ProjectAPI
) {

    suspend fun getProjects(authToken: String) =
        projectAPI.getProjects(authToken).body()?.attributes
}