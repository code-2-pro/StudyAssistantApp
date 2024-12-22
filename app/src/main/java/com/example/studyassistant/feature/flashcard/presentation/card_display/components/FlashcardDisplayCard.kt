package com.example.studyassistant.feature.flashcard.presentation.card_display.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.studyassistant.feature.flashcard.domain.model.Flashcard

@Composable
fun FlashcardDisplayCard(
    flashcard: Flashcard,
    modifier: Modifier = Modifier
) {
    var rotated by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (rotated) 180f else 0f,
        animationSpec = tween(500),
        label = "rotation"
    )

    val animateFront by animateFloatAsState(
        targetValue = if (!rotated) 1f else 0f,
        animationSpec = tween(500),
        label = "animateFront"
    )

    val animateBack by animateFloatAsState(
        targetValue = if (rotated) 1f else 0f,
        animationSpec = tween(500),
        label = "animateBack"
    )

    val animateColor by animateColorAsState(
        targetValue = if (rotated) {
            MaterialTheme.colorScheme.secondary
        } else MaterialTheme.colorScheme.primary,
        animationSpec = tween(500),
        label = "animateColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(700.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 8 * density
                }
                .clickable {
                    rotated = !rotated
                },
            colors = CardDefaults.cardColors(containerColor = animateColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (rotated) {
                        flashcard.answer
                    } else flashcard.question,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.graphicsLayer {
                            alpha = if (rotated) animateBack else animateFront
                            rotationY = rotation
                        }
                )
            }

        }
    }
}
