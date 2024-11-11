package com.example.studyassistant.presentation.session

import androidx.lifecycle.ViewModel
import com.example.studyassistant.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class SessionViewModel(
    private val sessionRepository: SessionRepository
): ViewModel() {

}