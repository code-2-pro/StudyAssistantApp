package com.example.studyassistant.feature.flashcard.presentation.category_detail

import androidx.compose.ui.graphics.Color
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
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    private val categoryRepository: FlashcardCategoryRepository,
    private val flashcardRepository: FlashcardRepository,
    private val authRepository: AuthRepository,
    private val generativeModel: GenerativeModel,
    private val navigator: Navigator,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val navArgs = savedStateHandle.toRoute<Route.CategoryDetailScreen>()

    private val _state = MutableStateFlow(CategoryDetailState())
    val state = combine(
        _state,
        flashcardRepository.getFlashcardsForCategory(navArgs.categoryId)
    ) { state, flashcards->
        state.copy(flashcards = flashcards)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = CategoryDetailState()
    )

    val isOnline: StateFlow<Boolean> = connectivityObserver.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    init {
        fetchCategory()
    }

    fun onAction(action: CategoryDetailAction) {
        when (action) {
            is CategoryDetailAction.GoToFlashcardScreen -> goToFlashcardScreen(action.flashcardId)
            is CategoryDetailAction.GenerateFlashcard -> generateFlashcard(action.cardQuantity)
        }
    }

    private fun generateFlashcard(cardQuantity: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            if (isOnline.value) {
                try {
                    // Generate content using the generative model
                    val response = generativeModel.generateContent(
                        "Generate exactly $cardQuantity flashcards about ${state.value.categoryName}. " +
                                "Each flashcard must have:\n" +
                                "1. A question and an answer.\n" +
                                "2. Both the question and the answer must contain a minimum of 2 characters and a maximum of 500 characters.\n" +
                                "Use the following format:\n" +
                                "1. Question: [Question]\n" +
                                "   Answer: [Answer]\n" +
                                "2. Question: [Question]\n" +
                                "   Answer: [Answer]"
                    )

                    // Validate response format
                    val flashcards = parseFlashcards(response.text.toString())
                    if (flashcards.size == cardQuantity) {
                        // Create new flashcards
                        flashcards.forEach { card ->
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
                            _state.update { it.copy(isLoading = false) }
                        }
                    } else {
                        SnackbarController.sendEvent(
                            event = SnackbarEvent(
                                message = "Failed to generate enough valid flashcards. Please try again."
                            )
                        )
                        _state.update { it.copy(isLoading = false) }
                    }
                } catch (e: Exception) {
                    // Log or handle generative model-specific issues
                    e.printStackTrace()
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = "Error generating flashcards: ${e.localizedMessage}"
                        )
                    )
                    _state.update { it.copy(isLoading = false) }
                }
            } else {
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "No internet connection. Flashcards cannot be generated."
                    )
                )
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun goToFlashcardScreen(flashcardId: String?) {
        viewModelScope.launch{
            navigator.navigate(Route.FlashcardScreen(
                flashcardId = flashcardId,
                categoryId = state.value.currentCategoryId
            ))
        }
    }

    private fun fetchCategory() {
        viewModelScope.launch {
            categoryRepository.getCategoryById(navArgs.categoryId).collect { category ->
                category?.let {
                    _state.update {
                        it.copy(
                            currentCategoryId = category.categoryId,
                            categoryName = category.name,
                            hasMeaning = category.isMeaningful,
                            categoryCardColors = category.colors.map { Color(it) }
                        )
                    }
                }
            }
        }
    }

    // Function to parse and validate flashcards from the response
    private fun parseFlashcards(response: String): List<Flashcard> {
        val flashcards = mutableListOf<Flashcard>()

        // Regex to match the expected format
        val regex = Regex("""\d+\.\s*Question:\s*(.*?)\n\s*Answer:\s*(.*?)\n""")
        val matches = regex.findAll(response)

        // Extract questions and answers
        matches.forEach { match ->
            val question = match.groups[1]?.value?.trim()
            val answer = match.groups[2]?.value?.trim()

            if (!question.isNullOrEmpty() && !answer.isNullOrEmpty()) {
                flashcards.add(Flashcard(
                    question = question,
                    answer = answer,
                    flashcardCategoryId = state.value.currentCategoryId,
                    createdAt = System.currentTimeMillis(),
                    flashcardId = UUID.randomUUID().toString(),
                ))
            }
        }
        return flashcards
    }


}
