package com.example.studyassistant.feature.utility.presentation.assistant

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyassistant.BuildConfig
import com.example.studyassistant.core.domain.ConnectivityObserver
import com.example.studyassistant.feature.utility.domain.MessageModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val connectivityObserver: ConnectivityObserver
): ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig { maxOutputTokens = 1024 },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
        )
    )

    fun sendMessage(question: String) {
        viewModelScope.launch{
            try {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        Log.i("History: " , it.message)
                        content(it.role) { text(it.message) }
                    }.toList()
                )
                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("Typing...", "model"))

                val response = chat.sendMessage(question)
                messageList.removeLastOrNull()
                messageList.add(MessageModel(response.text.toString(), "model"))
            }catch (e: Exception){
                messageList.removeLastOrNull()
                messageList.add(MessageModel("Error: "+e.message.toString(), "model"))
            }

        }
    }

}