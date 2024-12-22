package com.example.studyassistant.feature.flashcard.presentation.category.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.studyassistant.R
import com.example.studyassistant.feature.flashcard.domain.model.CategoryWithFlashcardCount

val categoryDropdownsItems = listOf(
    DropDownItem("View Detail", DropDownAction.VIEW_DETAIL),
    DropDownItem("Edit", DropDownAction.EDIT),
    DropDownItem("Delete", DropDownAction.DELETE)
)

fun LazyListScope.categoryList(
    sectionTitle: String,
    emptyListText: String,
    categories: List<CategoryWithFlashcardCount>,
    onDropdownItemClick: (DropDownItem, String, String) -> Unit,
    onViewAllCardsClick: (String) -> Unit,
    onAddIconClick: () -> Unit,
    modifier: Modifier = Modifier
){
    item {
        Column(
            modifier = modifier
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = sectionTitle,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
                IconButton(onClick = { onAddIconClick() }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Category"
                    )
                }
            }
        }
    }
    if(categories.isEmpty()){
        item{
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(R.drawable.img_books),
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
    items(categories){ category ->
        CategoryCard(
            categoryName = category.name,
            categoryCardColor = category.colors.map { Color(it) },
            totalCards = category.totalCards,
            dropdownItems = categoryDropdownsItems,
            onItemClick = { it
                onDropdownItemClick(it, category.categoryId, category.name)
            },
            onViewAllCardsClick = { onViewAllCardsClick(category.categoryId) },
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 4.dp
            )
        )
    }
}