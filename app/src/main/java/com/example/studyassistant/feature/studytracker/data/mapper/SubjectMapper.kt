package com.example.studyassistant.feature.studytracker.data.mapper

import com.example.studyassistant.feature.authentication.data.dto.RemoteSubject
import com.example.studyassistant.feature.studytracker.data.local.entity.SubjectEntity
import com.example.studyassistant.feature.studytracker.domain.model.Subject

fun SubjectEntity.toSubject(): Subject{
    return Subject(
        name = name,
        goalHours = goalHours,
        colors = colors,
        subjectId = subjectId
    )
}

fun Subject.toSubjectEntity(): SubjectEntity{
    return SubjectEntity(
        name = name,
        goalHours = goalHours,
        colors = colors,
        subjectId = subjectId
    )
}

fun Subject.toRemoteSubject(userId: String): RemoteSubject{
    return RemoteSubject(
        userId = userId,
        name = name,
        goalHours = goalHours,
        colors = colors,
        subjectId = subjectId
    )
}

fun RemoteSubject.toSubject(): Subject{
    return Subject(
        name = name,
        goalHours = goalHours,
        colors = colors,
        subjectId = subjectId
    )
}




