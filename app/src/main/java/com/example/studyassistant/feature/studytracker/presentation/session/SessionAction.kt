package com.example.studyassistant.feature.studytracker.presentation.session

import com.example.studyassistant.feature.studytracker.domain.model.Session
import com.example.studyassistant.feature.studytracker.domain.model.Subject

sealed interface SessionAction {
    data class OnRelatedSubjectChange(val subject: Subject) : SessionAction
    data class SaveSession(val duration: Long) : SessionAction
    data class OnDeleteSessionButtonClick(val session: Session) : SessionAction
    object DeleteSession : SessionAction
    object NotifyToUpdateSubject: SessionAction
    data class UpdateSubjectIdAndRelatedSubject(
        val subjectId: String?,
        val relatedToSubject: String?
    ) : SessionAction
}