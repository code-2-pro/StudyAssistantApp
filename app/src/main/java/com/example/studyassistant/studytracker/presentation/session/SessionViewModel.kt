package com.example.studyassistant.studytracker.presentation.session

import androidx.lifecycle.ViewModel
import com.example.studyassistant.studytracker.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
): ViewModel() {

}