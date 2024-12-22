package com.example.studyassistant.feature.flashcard.presentation.category

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.studyassistant.feature.flashcard.presentation.category.components.DropDownAction
import com.example.studyassistant.feature.flashcard.presentation.category.components.SaveCategoryDialog
import com.example.studyassistant.feature.flashcard.presentation.category.components.categoryList
import com.example.studyassistant.feature.studytracker.presentation.components.DeleteDialog

@Composable
fun CategoryScreen(
    state: CategoryState,
    onAction: (CategoryAction) -> Unit,
    modifier: Modifier = Modifier
) {

    var isSaveCategoryDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteCategoryDialogOpen by rememberSaveable { mutableStateOf(false) }
    var currentCategoryId by remember { mutableStateOf<String>("") }
    var previousName by remember { mutableStateOf<String>("") }
    var previousColors by remember { mutableStateOf<List<Color>> (emptyList()) }

    LaunchedEffect(isSaveCategoryDialogOpen) {
        if(isSaveCategoryDialogOpen){
            previousName = state.categoryName
            previousColors = state.categoryCardColors
        }else{
            previousName = ""
            previousColors = emptyList()
        }
    }

    SaveCategoryDialog(
        isOpen = isSaveCategoryDialogOpen,
        selectedColors = state.categoryCardColors,
        categoryName = state.categoryName,
        onColorChange = { onAction(CategoryAction.OnCategoryCardColorChange(it)) },
        onCategoryNameChange = { onAction(CategoryAction.OnCategoryNameChange(it)) },
        onDismissRequest = {
            currentCategoryId= ""
            onAction(CategoryAction.OnCancelCategoryChanges(
                previousName = previousName,
                previousColor = previousColors
            ))
            isSaveCategoryDialogOpen = false
        },
        onConfirmationButtonClick = {
            if(currentCategoryId.isBlank()){
                onAction(CategoryAction.SaveCategory)
            }else{
                onAction(CategoryAction.UpdateCategory(currentCategoryId, previousName))
            }
            currentCategoryId= ""
            isSaveCategoryDialogOpen = false
        }
    )

    DeleteDialog(
        title = "Delete Category?",
        bodyText = "Are you sure, you want to delete this category?" +
                " All the related flashcards will be permanently removed." +
                " This action can not be undone.",
        isOpen = isDeleteCategoryDialogOpen,
        onDismissRequest = {
            currentCategoryId= ""
            isDeleteCategoryDialogOpen = false
        },
        onConfirmationButtonClick = {
            onAction(CategoryAction.DeleteCategory(currentCategoryId))
            currentCategoryId= ""
            isDeleteCategoryDialogOpen = false
        }
    )
    if(state.isLoading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }else{
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            categoryList(
                modifier = Modifier.fillMaxWidth(),
                sectionTitle = "FLASHCARD CATEGORIES",
                emptyListText = "You don't have any categories yet.\n " +
                        "Click the + button to add new category.",
                categories = state.categoriesWithFlashcardCount,
                onDropdownItemClick = { selectedItem, categoryId, categoryName->
                    when (selectedItem.action) {
                        DropDownAction.VIEW_DETAIL -> {
                            onAction(CategoryAction.GoToCategoryDetail(categoryId))
                        }
                        DropDownAction.EDIT -> {
                            currentCategoryId = categoryId
                            onAction(CategoryAction.OnCategoryNameChange(categoryName))
                            isSaveCategoryDialogOpen = true
                        }
                        DropDownAction.DELETE -> {
                            currentCategoryId = categoryId
                            isDeleteCategoryDialogOpen = true
                        }
                    }
                },
                onViewAllCardsClick = { categoryId ->
                    onAction(CategoryAction.ShowFlashcards(categoryId))
                },
                onAddIconClick = {
                    currentCategoryId = ""
                    onAction(CategoryAction.OnCategoryNameChange(""))
                    isSaveCategoryDialogOpen = true
                }
            )
        }
    }
}