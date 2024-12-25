package com.example.studyassistant.feature.flashcard.presentation.category

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyassistant.core.domain.ConnectivityObserver
import com.example.studyassistant.core.navigation.Navigator
import com.example.studyassistant.core.navigation.Route
import com.example.studyassistant.core.presentation.util.SnackbarController
import com.example.studyassistant.core.presentation.util.SnackbarEvent
import com.example.studyassistant.feature.authentication.domain.repository.AuthRepository
import com.example.studyassistant.feature.flashcard.domain.model.FlashcardCategory
import com.example.studyassistant.feature.flashcard.domain.repository.FlashcardCategoryRepository
import com.example.studyassistant.feature.flashcard.domain.repository.FlashcardRepository
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    private val categoryRepository: FlashcardCategoryRepository,
    private val flashcardRepository: FlashcardRepository,
    private val authRepository: AuthRepository,
    private val generativeModel: GenerativeModel,
    private val navigator: Navigator
): ViewModel() {

    private val _state = MutableStateFlow(CategoryState())
    val state = combine(
        _state,
        categoryRepository.getCategoryWithFlashcardCount(),
    ){ state, categoriesWithFlashcardCount ->
        state.copy(
            categoriesWithFlashcardCount = categoriesWithFlashcardCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = CategoryState()
    )

    val isOnline: StateFlow<Boolean> = connectivityObserver.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    fun onAction(action: CategoryAction){
        when(action){
            is CategoryAction.OnCategoryNameChange -> {
                _state.update {
                    it.copy(categoryName = action.name)
                }
            }
            is CategoryAction.OnCategoryCardColorChange -> {
                _state.update {
                    it.copy(categoryCardColors = action.color)
                }
            }
            is CategoryAction.OnCancelCategoryChanges -> {
                _state.update {
                    it.copy(
                        categoryName = action.previousName,
                        categoryCardColors = action.previousColor
                    )
                }
            }
            CategoryAction.SaveCategory -> {
                _state.update {
                    it.copy(currentCategoryId = "")
                }
                saveCategory()
            }
            is CategoryAction.UpdateCategory -> {
                _state.update {
                    it.copy(
                        currentCategoryId = action.categoryId,
                        categoryPreviousName = action.categoryPreviousName
                    )
                }
                updateCategory()
            }
            is CategoryAction.DeleteCategory -> {
                _state.update {
                    it.copy(currentCategoryId = action.categoryId)
                }
                deleteCategory()
            }
            is CategoryAction.GoToCategoryDetail -> {
                _state.update {
                    it.copy(
                        currentCategoryId = action.categoryId
                    )
                }
                goToCategoryDetail()
            }
            is CategoryAction.ShowFlashcards -> {
                _state.update {
                    it.copy(currentCategoryId = action.categoryId)
                }
                showFlashcards()
            }
        }
    }

    private fun showFlashcards() {
        viewModelScope.launch{
            val categoryId = state.value.currentCategoryId
            val totalFlashcardInCategory =
                flashcardRepository.getTotalFlashcardCountForCategory(categoryId)
            if(totalFlashcardInCategory.first() <= 0){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "No Flashcards to show."
                    )
                )
                return@launch
            }else{
                navigator.navigate(Route.FlashcardDisplayScreen(categoryId))
            }
        }
    }

    private fun goToCategoryDetail(){
        viewModelScope.launch{
            navigator.navigate(Route.CategoryDetailScreen(
                categoryId = state.value.currentCategoryId
            ))
        }
    }

    private fun saveCategory() {
        viewModelScope.launch {
            try {
                val hasMeaning = checkHasMeaning()
                val category = FlashcardCategory(
                    categoryId = UUID.randomUUID().toString(),
                    name = state.value.categoryName,
                    colors = state.value.categoryCardColors.map { it.toArgb() },
                    isMeaningful = hasMeaning
                )
                categoryRepository.upsertCategory(category)

                val currentUser = authRepository.getCurrentUser()
                if (isOnline.value && currentUser != null) {
                    categoryRepository.upsertCategoryOnRemote(
                        category = category,
                        userId = currentUser.userId.toString()
                    )
                }

                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Category saved successfully."
                    )
                )
            } catch (e: Exception) {
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Couldn't save category. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun updateCategory() {
        viewModelScope.launch {
            try {
                val isPreviousName = state.value.categoryPreviousName == state.value.categoryName
                val hasMeaning = if(isPreviousName){
                    false
                }else checkHasMeaning()
                val category = FlashcardCategory(
                    categoryId = state.value.currentCategoryId,
                    name = state.value.categoryName,
                    colors = state.value.categoryCardColors.map { it.toArgb() },
                    isMeaningful = hasMeaning
                )
                categoryRepository.upsertCategory(category)

                val currentUser = authRepository.getCurrentUser()
                if (isOnline.value && currentUser != null) {
                    categoryRepository.upsertCategoryOnRemote(
                        category = category,
                        userId = currentUser.userId.toString()
                    )
                }
                _state.update { it.copy(categoryPreviousName = "") }
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Category updated successfully."
                    )
                )
            } catch (e: Exception) {
                _state.update { it.copy(categoryPreviousName = "") }
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Couldn't update category. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun deleteCategory(){
        viewModelScope.launch{
            try {
                val currentCategoryId = state.value.currentCategoryId
                if(currentCategoryId.isNotBlank()){
                    withContext(Dispatchers.IO) {
                        categoryRepository.deleteCategory(categoryId = currentCategoryId)
                        val currentUser = authRepository.getCurrentUser()
                        if(isOnline.value && currentUser != null){
                            categoryRepository.deleteCategoryOnRemote(
                                categoryId = currentCategoryId,
                                userId = currentUser.userId.toString()
                            )
                        }
                    }
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = "Category deleted successfully."
                        )
                    )
                }else{
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = "No Category to delete."
                        )
                    )
                }
            }catch (e: Exception){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Couldn't delete category. ${e.message}",
                        duration =  SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private suspend fun checkHasMeaning(): Boolean {
        _state.update { it.copy(isLoading =  true) }
        // Assume this value comes from some state or observable source
        val isOnlineValue = isOnline.value

        if (!isOnlineValue) {
            _state.update { it.copy(isLoading =  false) }
            return false // No internet connection, so we assume no meaning
        }

        return try {
            val response = withContext(Dispatchers.IO) {
                Log.e("CheckHasMeaningError", "Error: ${state.value.categoryName}")
                generativeModel.generateContent(
                    "Response only true if the following string has meaning, " +
                            "else only false. String: ${state.value.categoryName}"
                )
//                generativeModel.generateContent(
//                    "Respond only true if the following string meets all these requirements: " +
//                            "1. The string is clear and has meaning. " +
//                            "2. The string could be used to create a concise educational question-answer pair. " +
//                            "If any of these conditions are not met, respond with false. String: '${state.value.categoryName}'"
//                )
            }
            Log.e("CheckHasMeaningError", response.text.toString())
            _state.update { it.copy(isLoading =  false) }
            response.text.toString().trim().equals("true", ignoreCase = true)
        } catch (e: Exception) {
            _state.update { it.copy(isLoading =  false) }
            Log.e("CheckHasMeaningError", "Error: ${e.message}")
            false // Fallback in case of an error
        }
    }


}