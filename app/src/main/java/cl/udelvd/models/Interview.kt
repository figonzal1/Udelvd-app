package cl.udelvd.models

import java.util.*

data class Interview(
    var id: Int = 0,
    var idInterviewee: Int = 0,
    var interviewType: InterviewType? = null,
    var interviewDate: Date? = null
)

