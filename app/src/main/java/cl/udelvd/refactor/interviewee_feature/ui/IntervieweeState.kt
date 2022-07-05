package cl.udelvd.refactor.interviewee_feature.ui

import cl.udelvd.refactor.interviewee_feature.domain.model.Interviewee

data class IntervieweeState(
    val interviewee: List<Interviewee> = emptyList(),
    val isLoading: Boolean = true
)
