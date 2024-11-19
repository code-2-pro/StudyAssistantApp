package com.example.studyassistant.studytracker.data.mapper

import com.example.studyassistant.studytracker.data.local.entity.SubjectEntity
import com.example.studyassistant.studytracker.domain.model.Subject

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