package com.example.studyassistant.presentation.dashboard

import androidx.lifecycle.ViewModel
import com.example.studyassistant.data.local.AppDatabase
import com.example.studyassistant.domain.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository
): ViewModel() {

}