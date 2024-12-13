package com.example.studyassistant.feature.utility.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.studyassistant.R
import com.example.studyassistant.feature.utility.presentation.components.UtilityListItem

@Composable
fun UtilityScreen(
    onAssistantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    )
    {
        UtilityListItem(
            title = "AI Assistant",
            description = "Chat with Gemini",
            icon = painterResource(R.drawable.baseline_assistant_24),
            onClick = { onAssistantClick() },
            modifier = Modifier.fillMaxWidth()
        )
        HorizontalDivider()

        UtilityListItem(
            title = "Document Analyzer",
            description = "Scan Document with ML Kit",
            icon = painterResource(R.drawable.baseline_document_scanner_24),
            onClick = {  },
            modifier = Modifier.fillMaxWidth()
        )
        HorizontalDivider()
    }
}