package com.example.studyassistant.feature.flashcard.presentation.category.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyassistant.R
import com.example.studyassistant.ui.theme.StudyAssistantTheme
import com.example.studyassistant.ui.theme.gradient5

data class DropDownItem(
    val text: String,
    val action: DropDownAction // Enum for possible actions
)

enum class DropDownAction {
    VIEW_DETAIL,
    EDIT,
    DELETE
}

@Composable
fun CategoryCard(
    categoryName: String,
    categoryCardColor: List<Color>,
    totalCards: Int,
    dropdownItems: List<DropDownItem>,
    onItemClick: (DropDownItem) -> Unit,
    onViewAllCardsClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val density = LocalDensity.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 10.dp,
        modifier = Modifier
            .height(210.dp)
            .padding(10.dp)
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            }
            .indication(interactionSource, LocalIndication.current)
            .pointerInput(true) {
                detectTapGestures(
                    onLongPress = {
                        isContextMenuVisible = true
                        pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                    },
                    onPress = {
                        val press = PressInteraction.Press(it)
                        interactionSource.emit(press)
                        tryAwaitRelease()
                        interactionSource.emit(PressInteraction.Release(press))
                    }
                )
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(2f),
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.wrapContentSize(),
                ) {
                    Text(
                        text = "Flashcard",
                        fontSize =  12.sp,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = categoryName,
                    fontSize =  24.sp,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(text = "Total Cards : $totalCards")

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    shape = RoundedCornerShape(8.dp),
                    onClick = onViewAllCardsClick
                ) {
                    Text(
                        text = "View All",
                        fontSize =  11.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .size(width = 100.dp, height = 140.dp)
                    .background(
                        brush = Brush.verticalGradient(categoryCardColor),
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_books),
                    contentScale = ContentScale.Inside,
                    contentDescription = null
                )
            }
        }
        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = {
                isContextMenuVisible = false
            },
            offset = pressOffset.copy(
                y = pressOffset.y - itemHeight
            )
        ) {
            dropdownItems.forEach {
                DropdownMenuItem(
                    onClick = {
                        onItemClick(it)
                        isContextMenuVisible = false
                    },
                    text = { Text(text = it.text) }
                )
            }
        }
    }
}


@Preview
@Composable
private fun FlashcardSectionPreview() {
    StudyAssistantTheme {
        CategoryCard(
            categoryName = "Math",
            totalCards = 100,
            categoryCardColor = gradient5,
            dropdownItems = listOf(
                DropDownItem(text = "View Detail", action = DropDownAction.VIEW_DETAIL),
                DropDownItem(text = "Edit", action = DropDownAction.EDIT),
                DropDownItem(text = "Delete", action = DropDownAction.DELETE)
            ),
            onItemClick = {},
            onViewAllCardsClick = {}
        )
    }
}