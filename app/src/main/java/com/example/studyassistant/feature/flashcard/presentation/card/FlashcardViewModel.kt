package com.example.studyassistant.feature.flashcard.presentation.card

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.studyassistant.core.domain.ConnectivityObserver
import com.example.studyassistant.core.navigation.Navigator
import com.example.studyassistant.core.navigation.Route
import com.example.studyassistant.core.presentation.util.SnackbarController
import com.example.studyassistant.core.presentation.util.SnackbarEvent
import com.example.studyassistant.feature.authentication.domain.repository.AuthRepository
import com.example.studyassistant.feature.flashcard.domain.model.Flashcard
import com.example.studyassistant.feature.flashcard.domain.repository.FlashcardCategoryRepository
import com.example.studyassistant.feature.flashcard.domain.repository.FlashcardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    private val authRepository: AuthRepository,
    private val flashcardRepository: FlashcardRepository,
    private val categoryRepository: FlashcardCategoryRepository,
    private val navigator: Navigator,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val navArgs = savedStateHandle.toRoute<Route.FlashcardScreen>()

    private val _state = MutableStateFlow(FlashcardState())
    val state = _state.asStateFlow()

    val isOnline: StateFlow<Boolean> = connectivityObserver.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    init {
        fetchFlashcard()
        fetchCategory()
    }

    fun onAction(action: FlashcardAction){
        when(action){
            is FlashcardAction.OnQuestionChange -> {
                _state.update {
                    it.copy(question = action.question)
                }
            }
            is FlashcardAction.OnAnswerChange -> {
                _state.update {
                    it.copy(answer = action.answer)
                }
            }
            FlashcardAction.SaveFlashcard -> saveFlashcard()
            FlashcardAction.DeleteFlashcard -> deleteFlashcard()
        }
    }

    private fun saveFlashcard() {
        viewModelScope.launch{
            try {
                val state = _state.value
                val card = Flashcard(
                    question = state.question,
                    answer = state.answer,
                    flashcardCategoryId = state.flashcardCategoryId,
                    createdAt = System.currentTimeMillis(),
                    flashcardId = if(state.currentFlashcardId.isBlank()) {
                        UUID.randomUUID().toString()
                    }else state.currentFlashcardId
                )
                flashcardRepository.upsertFlashcard(card)
                val currentUser = authRepository.getCurrentUser()
                if(isOnline.value && currentUser != null){
                    flashcardRepository.upsertFlashcardOnRemote(
                        flashcard = card,
                        userId = currentUser.userId.toString()
                    )
                }
                SnackbarController.sendEvent(
                    event = SnackbarEvent(message = "Flashcard Saved Successfully.")
                )
                navigator.navigateUp()
            }catch (e: Exception){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Couldn't save Flashcard. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun deleteFlashcard() {
        viewModelScope.launch{
            try {
                val currentFlashcardId = state.value.currentFlashcardId
                if(currentFlashcardId.isNotBlank()){
                    withContext(Dispatchers.IO) {
                        flashcardRepository.deleteFlashcard(flashcardId = currentFlashcardId)
                        val currentUser = authRepository.getCurrentUser()
                        if(isOnline.value && currentUser != null){
                            flashcardRepository.deleteFlashcardOnRemote(
                                flashcardId = currentFlashcardId,
                                userId = currentUser.userId.toString()
                            )
                        }
                    }
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = "Flashcard deleted successfully."
                        )
                    )
                    navigator.navigateUp()
                }else{
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = "No Flashcard to delete."
                        )
                    )
                }
            }catch (e: Exception){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Couldn't delete Flashcard. ${e.message}",
                        duration =  SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun fetchFlashcard(){
        viewModelScope.launch{
            navArgs.flashcardId?.let { id ->
                flashcardRepository.getFlashcardById(id).collect{card ->
                    card?.let {
                        _state.update {
                            it.copy(
                                question = card.question,
                                answer = card.answer,
                                flashcardCategoryId = card.flashcardCategoryId,
                                currentFlashcardId = card.flashcardId
                            )
                        }
                    }
                }
            }
        }
    }

    private fun fetchCategory(){
        viewModelScope.launch{
            navArgs.categoryId?.let { id ->
                categoryRepository.getCategoryById(id).collect{ category ->
                    category?.let {
                        _state.update {
                            it.copy(
                                flashcardCategoryId = category.categoryId,
                                relatedToCategory = category.name
                            )
                        }
                    }
                }
            }
        }
    }

}