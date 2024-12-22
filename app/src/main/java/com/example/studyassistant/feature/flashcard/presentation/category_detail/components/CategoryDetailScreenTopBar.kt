@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studyassistant.feature.flashcard.presentation.category_detail.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyassistant.feature.flashcard.presentation.category_detail.CategoryDetailState

@Composable
fun CategoryDetailScreenTopBar(
    state: CategoryDetailState,
    isOnline: Boolean,
    onBackButtonClicked: () -> Unit,
    onGenerateCardClicks: () -> Unit
){
    TopAppBar(
        navigationIcon = {
            IconButton(
                enabled = !state.isLoading,
                onClick = onBackButtonClicked
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate Back"
                )
            }
        },
        title = {
            Text(
                text = state.categoryName,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        actions = {
            if(state.hasMeaning && state.flashcards.isEmpty() && isOnline){
                OutlinedButton(
                    modifier = Modifier.padding(end = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = onGenerateCardClicks,
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "Generate Flashcard",
                        fontSize =  11.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    )
}