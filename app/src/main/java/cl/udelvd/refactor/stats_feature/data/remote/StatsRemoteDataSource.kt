package cl.udelvd.refactor.stats_feature.data.remote

import cl.udelvd.refactor.interviewee_feature.domain.model.Interviewee

class StatsRemoteDataSource(
    private val statsAPI: StatsAPI
) {

    suspend fun getStats(
        authToken: String,
        idSelectedEmoticon: Int,
        genreLetter: String,
        filterInterviewees: List<Interviewee>
    ): StatsAttributesResult? = when {

        idSelectedEmoticon != -1 && genreLetter != "" && filterInterviewees.isNotEmpty() -> {
            val ids = processFilterInterviewees(filterInterviewees)
            statsAPI.getStatsByEmoticonAndGenreAndInterviewees(idSelectedEmoticon, genreLetter, ids)
        }
        idSelectedEmoticon != -1 && genreLetter != "" -> {
            statsAPI.getStatsByEmoticonAndGenre(idSelectedEmoticon, genreLetter)
        }
        idSelectedEmoticon != -1 && filterInterviewees.isNotEmpty() -> {
            val ids = processFilterInterviewees(filterInterviewees)
            statsAPI.getStatsByEmoticonAndInterviewees(idSelectedEmoticon, ids)
        }
        genreLetter != "" && filterInterviewees.isNotEmpty() -> {
            val ids = processFilterInterviewees(filterInterviewees)
            statsAPI.getStatsByGenreAndInterviewees(genreLetter, ids)
        }
        idSelectedEmoticon != -1 -> statsAPI.getStatsByEmoticon(idSelectedEmoticon)
        genreLetter != "" -> statsAPI.getStatsByGenre(genreLetter)
        filterInterviewees.isNotEmpty() -> {
            val ids = processFilterInterviewees(filterInterviewees)
            statsAPI.getStatsByInterviewees(ids)
        }
        else -> statsAPI.getStats()

    }.body()?.data?.first()?.attributes

    private fun processFilterInterviewees(filterInterviewees: List<Interviewee>): String {
        var ids = ""
        for (i in filterInterviewees.indices) {

            when {
                i != filterInterviewees.size - 1 -> ids += "${filterInterviewees[i].id};"
                i == filterInterviewees.size - 1 -> ids += "${filterInterviewees[i].id}"
            }
        }

        return ids
    }
}