package com.example.studyassistant.feature.utility.presentation.assistant.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studyassistant.feature.utility.domain.MessageModel
import com.example.studyassistant.ui.theme.ColorModelMessage
import com.example.studyassistant.ui.theme.ColorUserMessage

@Composable
fun MessageRow(
    messageModel: MessageModel,
    modifier: Modifier = Modifier
) {
    val isModel = messageModel.role == "model"

    Row(
       verticalAlignment = Alignment.CenterVertically
    ){
        Box (
            modifier = Modifier.fillMaxWidth()
        ){
            Box(
                modifier = Modifier.align(
                    if(isModel) Alignment.BottomStart else Alignment.BottomEnd
                ).padding(
                    start = if(isModel) 8.dp else 70.dp,
                    end = if(isModel) 70.dp else 8.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
                    .clip(RoundedCornerShape(48f))
                    .background(if(isModel) ColorModelMessage else ColorUserMessage)
                    .padding(16.dp)
            ){
                SelectionContainer {
                    Text(
                        text = messageModel.message,
                        fontWeight = FontWeight.W500,
                        color = Color.White
                    )
                }
            }
        }
    }
}