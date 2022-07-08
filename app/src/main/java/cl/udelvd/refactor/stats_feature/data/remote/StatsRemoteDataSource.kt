package cl.udelvd.refactor.stats_feature.data.remote

import cl.udelvd.refactor.core.utils.processFilterIds
import cl.udelvd.refactor.interviewee_feature.domain.model.Interviewee
import cl.udelvd.refactor.project_feature.domain.model.Project

class StatsRemoteDataSource(
    private val statsAPI: StatsAPI
) {

    suspend fun getStats(
        authToken: String,
        idSelectedEmoticon: Int,
        genreLetter: String,
        selectedProjects: List<Project>,
        selectedInterviewees: List<Interviewee>
    ): StatsAttributesResult? = when {

        /**
         * FilteR: emoticon, genre, interviewee, projects
         */
        idSelectedEmoticon != -1 && genreLetter != "" && selectedInterviewees.isNotEmpty() && selectedProjects.isNotEmpty() -> {
            val projectsIds = processFilterIds(selectedProjects)
            val intervieweeId = processFilterIds(selectedInterviewees)
            statsAPI.getStatsByEmoticonAndGenreAndIntervieweesAndProjects(
                idSelectedEmoticon,
                genreLetter,
                projectsIds,
                intervieweeId
            )
        }

        /**
         * Filter: emoticon, genre, interviewee
         */
        idSelectedEmoticon != -1 && genreLetter != "" && selectedInterviewees.isNotEmpty() -> {
            val ids = processFilterIds(selectedInterviewees)
            statsAPI.getStatsByEmoticonAndGenreAndInterviewees(idSelectedEmoticon, genreLetter, ids)
        }

        /**
         * Filter: emoticon, genre, projects
         */
        idSelectedEmoticon != -1 && genreLetter != "" && selectedProjects.isNotEmpty() -> {
            val projectsIds = processFilterIds(selectedProjects)
            statsAPI.getStatsByEmoticonAndGenreAndProjects(
                idSelectedEmoticon,
                genreLetter,
                projectsIds
            )
        }

        /**
         * Filter: emoticon, interviewee, project
         */
        idSelectedEmoticon != -1 && selectedInterviewees.isNotEmpty() && selectedProjects.isNotEmpty() -> {
            val intervieweeIds = processFilterIds(selectedInterviewees)
            val projectsIds = processFilterIds(selectedProjects)
            statsAPI.getStatsByEmoticonAndIntervieweesAndProjects(
                idSelectedEmoticon,
                projectsIds,
                intervieweeIds
            )
        }

        /**
         * Filter: genre, interviewee, project
         */
        genreLetter != "" && selectedInterviewees.isNotEmpty() && selectedProjects.isNotEmpty() -> {
            val intervieweeIds = processFilterIds(selectedInterviewees)
            val projectsIds = processFilterIds(selectedProjects)
            statsAPI.getStatsByGenreAndIntervieweesAndProjects(
                genreLetter,
                projectsIds,
                intervieweeIds
            )
        }

        /**
         * Filter: emoticon, projects
         */
        idSelectedEmoticon != -1 && selectedProjects.isNotEmpty() -> {
            val projectsIds = processFilterIds(selectedProjects)
            statsAPI.getStatsByEmoticonAndProjects(idSelectedEmoticon, projectsIds)
        }
        /**
         * Filter: genre, projects
         */
        genreLetter != "" && selectedProjects.isNotEmpty() -> {
            val projectsIds = processFilterIds(selectedProjects)
            statsAPI.getStatsByGenreAndProjects(genreLetter, projectsIds)
        }

        /**
         * Filter: interviewee, projects
         */
        selectedInterviewees.isNotEmpty() && selectedProjects.isNotEmpty() -> {
            val projectIds = processFilterIds(selectedProjects)
            val intervieweeIds = processFilterIds(selectedInterviewees)
            statsAPI.getStatsByIntervieweesAndProjects(projectIds, intervieweeIds)
        }

        /**
         * Filter: emoticon, genero
         */
        idSelectedEmoticon != -1 && genreLetter != "" -> {
            statsAPI.getStatsByEmoticonAndGenre(idSelectedEmoticon, genreLetter)
        }

        /**
         * Filter: emoticon, interviewee
         */
        idSelectedEmoticon != -1 && selectedInterviewees.isNotEmpty() -> {
            val ids = processFilterIds(selectedInterviewees)
            statsAPI.getStatsByEmoticonAndInterviewees(idSelectedEmoticon, ids)
        }

        /**
         * Filter: genero, interviewee
         */
        genreLetter != "" && selectedInterviewees.isNotEmpty() -> {
            val ids = processFilterIds(selectedInterviewees)
            statsAPI.getStatsByGenreAndInterviewees(genreLetter, ids)
        }

        /**
         * Filter: emoticons
         */
        idSelectedEmoticon != -1 -> statsAPI.getStatsByEmoticon(idSelectedEmoticon)

        /**
         * Filter: Genero
         */
        genreLetter != "" -> statsAPI.getStatsByGenre(genreLetter)

        /**
         * Filter: interviewee
         */
        selectedInterviewees.isNotEmpty() -> {
            val ids = processFilterIds(selectedInterviewees)
            statsAPI.getStatsByInterviewees(ids)
        }

        /**
         * Filter: project
         */
        selectedProjects.isNotEmpty() -> {
            val ids = processFilterIds(selectedProjects)
            statsAPI.getStatsByProjects(ids)
        }

        /**
         * Filter: none
         */
        else -> statsAPI.getStats()

    }.body()?.data?.first()?.attributes


}