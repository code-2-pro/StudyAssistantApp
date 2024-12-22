package com.example.studyassistant.feature.setting.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studyassistant.R
import com.example.studyassistant.ui.theme.StudyAssistantTheme

@Composable
fun SwitchButton(
    title: String,
    icon: Painter,
    isDarkTheme: Boolean,
    onToggleChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Distribute items with space between
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
        Switch(
            checked = isDarkTheme,
            onCheckedChange = { onToggleChange() }
        )
    }
}

@Preview
@Composable
private fun SwitchButtonPreview() {
    StudyAssistantTheme {
        SwitchButton(
            title = "Dark Theme",
            icon = painterResource(R.drawable.baseline_dark_mode_24),
            isDarkTheme = true,
            onToggleChange = { }
        )
    }
}