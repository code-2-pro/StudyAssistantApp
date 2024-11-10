package com.example.studyassistant.data.mapper

import com.example.studyassistant.data.local.entity.SubjectEntity
import com.example.studyassistant.domain.model.Subject

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