package com.example.studyassistant.feature.utility.presentation.assistant

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyassistant.core.domain.ConnectivityObserver
import com.example.studyassistant.feature.utility.domain.MessageModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssistantViewModel @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    private val generativeModel: GenerativeModel
): ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    val isOnline: StateFlow<Boolean> = connectivityObserver.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    fun sendMessage(question: String) {
        viewModelScope.launch{
            if(isOnline.value){
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

}