package com.example.studyassistant.studytracker.presentation.session

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.studyassistant.MainActivity
import com.example.studyassistant.studytracker.presentation.util.Constants.CLICK_REQUEST_CODE
import com.example.studyassistant.studytracker.presentation.util.Constants.DEEPLINK_DOMAIN

object ServiceHelper {

    fun clickPendingIntent(context: Context): PendingIntent {
//        val deepLinkIntent = Intent(
//            Intent.ACTION_VIEW,
//            "study_assistant://dashboard/session".toUri(),
//            context,
//            MainActivity::class.java
//        )
        val deepLinkIntent = Intent(context, MainActivity::class.java).apply {
            data = "https://$DEEPLINK_DOMAIN".toUri()
        }
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(
                CLICK_REQUEST_CODE,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    fun triggerForegroundService(context: Context, action: String){
        Intent(context, StudySessionTimerService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}