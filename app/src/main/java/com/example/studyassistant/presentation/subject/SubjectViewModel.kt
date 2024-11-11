package com.example.studyassistant.presentation.subject

import androidx.lifecycle.ViewModel
import com.example.studyassistant.domain.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class SubjectViewModel(
    private val subjectRepository: SubjectRepository
): ViewModel() {

}