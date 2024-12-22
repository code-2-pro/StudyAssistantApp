package com.example.studyassistant.feature.flashcard.presentation.card.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.studyassistant.R
import com.example.studyassistant.feature.flashcard.domain.model.Flashcard

fun LazyListScope.flashcardList(
    sectionTitle: String,
    emptyListText: String,
    cards: List<Flashcard>,
    onEditButtonClick: (String?) -> Unit
){
    item{
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp)
        )
    }
    if(cards.isEmpty()){
        item{
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(R.drawable.img_tasks),
                    contentDescription = emptyListText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = emptyListText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    items(cards){ card ->
        FlashcardCard(
            question = card.question,
            answer = card.answer,
            onEditButtonClick = { onEditButtonClick(card.flashcardId) },
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 4.dp
            )
        )
    }

}
