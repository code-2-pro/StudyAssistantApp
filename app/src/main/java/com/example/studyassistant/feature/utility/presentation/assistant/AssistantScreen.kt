package com.example.studyassistant.feature.utility.presentation.assistant

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.studyassistant.feature.utility.domain.MessageModel
import com.example.studyassistant.feature.utility.presentation.assistant.components.MessageInput
import com.example.studyassistant.feature.utility.presentation.assistant.components.MessageList

@Composable
fun AssistantScreen(
    messageList: List<MessageModel>,
    onMessageSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        MessageList(
            messageList = messageList,
            modifier = Modifier.weight(1f)
        )
        MessageInput(
            onMessageSend = { onMessageSend(it) }
        )
    }
}

