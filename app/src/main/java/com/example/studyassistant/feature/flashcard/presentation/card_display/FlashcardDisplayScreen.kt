package com.example.studyassistant.feature.flashcard.presentation.card_display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.studyassistant.feature.flashcard.presentation.card_display.components.FlashcardDisplayCard
import kotlinx.coroutines.launch

@Composable
fun FlashcardDisplayScreen(
    state: FlashcardDisplayState
) {
    val pagerState = rememberPagerState(
        pageCount = { state.flashcards.size },
    )
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = state.categoryName,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        HorizontalPager(
            state = pagerState,
            key = { state.flashcards[it].flashcardId},
            pageSize = PageSize.Fill,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) { index ->
            FlashcardDisplayCard(
                flashcard = state.flashcards[index]
            )
        }
        Box(
            modifier = Modifier
                .offset(y = - (16).dp)
                .fillMaxWidth(0.5f)
                .clip(RoundedCornerShape(100))
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "${pagerState.currentPage + 1} / ${pagerState.pageCount}"
            )
            IconButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage - 1
                        )
                    }
                },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Go back"
                )
            }
            IconButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1
                        )
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Go forward"
                )
            }
        }
    }
}


