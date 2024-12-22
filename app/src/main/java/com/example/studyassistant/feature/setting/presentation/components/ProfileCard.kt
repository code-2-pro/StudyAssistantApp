package com.example.studyassistant.feature.setting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studyassistant.R
import com.example.studyassistant.ui.theme.StudyAssistantTheme

@Composable
fun ProfileCard(
    displayName: String,
    hasAccount: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                painter = if(hasAccount){
                    painterResource(id = R.drawable.baseline_account_circle_24)
                } else painterResource(id = R.drawable.baseline_no_accounts_24),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = if(displayName.isNotBlank()){
                    displayName
                } else  "No display name")
                Text(
                    text = if(hasAccount){
                        "Logged In"
                    } else "No Account Linked",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


@Preview
@Composable
private fun TestPreview() {
    StudyAssistantTheme {
        ProfileCard(
            displayName = "Emma",
            hasAccount = true,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth() // Fit to entire width of screen
                .padding(horizontal = 8.dp)
        )
    }
}