package com.example.studyassistant.feature.authentication.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyassistant.core.domain.ConnectivityObserver
import com.example.studyassistant.core.domain.util.AuthError
import com.example.studyassistant.core.domain.util.onError
import com.example.studyassistant.core.domain.util.onSuccess
import com.example.studyassistant.core.navigation.Navigator
import com.example.studyassistant.core.navigation.Route
import com.example.studyassistant.core.presentation.util.SnackbarController
import com.example.studyassistant.core.presentation.util.SnackbarEvent
import com.example.studyassistant.feature.authentication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val navigator: Navigator
): ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = combine(
        _state,
        connectivityObserver.isConnected,
        authRepository.checkHasLocalData()
    ){ state, isConnected, hasLocalData ->
        state.copy(
            isConnected = isConnected,
            hasLocalData = hasLocalData
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthState()
    )

    private val _events = Channel<AuthEvent>()
    val events = _events.receiveAsFlow()


    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        viewModelScope.launch{
            _state.update { it.copy(isLoading = true) }
            val user = authRepository.getCurrentUser()
            if(user != null){
                _state.update {
                    it.copy(currentUser = user)
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun catchRealtimeUpdate(){
        viewModelScope.launch{
            _state.update { it.copy(isLoading = true) }
            if(state.value.isConnected){
                authRepository.catchRealtimeUpdates()
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

     fun onAction(action: AuthAction){
        when(action){
            is AuthAction.OnLoginClick -> login(action.email, action.password)
            is AuthAction.OnRegisterClick -> {
                register(action.email, action.password, action.displayName)
            }
            AuthAction.OnToLoginPageClick -> goToLoginPage()
            AuthAction.OnToRegisterPageClick -> goToRegisterPage()
            AuthAction.OnGetDataFromRemoteClick -> {
                getDataFromRemote()
            }
            AuthAction.OnSendDataToRemoteClick -> {
                sendDataToRemote()
            }
            AuthAction.OnUseNoAccountClick -> {
                useNoAccount()
            }
            AuthAction.OnLogoutKeepLocalDataClick -> {
                logoutWithKeepLocalData()
            }
            AuthAction.OnLogoutRemoveLocalDataClick -> {
                logoutWithRemoveLocalData()
            }
        }
    }

    private fun logoutWithRemoveLocalData(){
        viewModelScope.launch{
            _state.update { it.copy(
                isLoading = true,
                currentUser = null
            ) }
            authRepository.logout()
            authRepository.removeAllLocalData()
            navigator.navigate(Route.Authentication){
                popUpTo(0){ // Clears the entire back stack
                    inclusive = true
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun logoutWithKeepLocalData(){
        viewModelScope.launch{
            _state.update { it.copy(
                isLoading = true,
                currentUser = null
            ) }
            authRepository.logout()
            navigator.navigate(
                route =  Route.Authentication,
                navOptions = {
                    popUpTo(0){
                        inclusive = true
                    }
                }
            )
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun sendDataToRemote() {
        viewModelScope.launch{
            _state.update { it.copy(isLoading = true) }
            authRepository.sendLocalDataToRemote()
            navigator.navigate(
                route =  Route.StudyTracker,
                navOptions = {
                    popUpTo(Route.Authentication) {
                        inclusive = false
                    }
                }
            )
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun getDataFromRemote() {
        viewModelScope.launch{
            _state.update { it.copy(isLoading = true) }
            authRepository.getRemoteDataForLocal()
            navigator.navigate(
                route =  Route.StudyTracker,
                navOptions = {
                    popUpTo(Route.Authentication) {
                        inclusive = true
                    }
                }
            )
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun useNoAccount(){
        viewModelScope.launch{
            _state.update { it.copy(isLoading = true) }
            if(state.value.currentUser != null){
                authRepository.logout()
                authRepository.removeAllLocalData()
                _state.update { it.copy(currentUser = null) }
                navigator.navigate(
                    route =  Route.StudyTracker,
                    navOptions = {
                        popUpTo<Route.StudyTracker>{inclusive = false}
                    }
                )
                _state.update { it.copy(isLoading = false) }
            }else{
                navigator.navigate(
                    route =  Route.StudyTracker,
                    navOptions = {
                        popUpTo<Route.StudyTracker>{inclusive = false}
                    }
                )
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun goToLoginPage(){
        viewModelScope.launch{
            navigator.navigate(Route.LoginScreen)
        }
    }

    private fun goToRegisterPage(){
        viewModelScope.launch{
            navigator.navigate(Route.RegisterScreen)
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank()) {
                _events.send(AuthEvent.AuthenticationError(AuthError.EMAIL_IS_BLANK))
                return@launch
            }
            if(password.isBlank()){
                _events.send(AuthEvent.AuthenticationError(AuthError.PASSWORD_IS_BLANK))
                return@launch
            }

            _state.update { it.copy(isLoading = true) }
            if(state.value.isConnected){
                authRepository.login(email, password)
                    .onSuccess {  user ->
                        authRepository.checkDataConsistency()
                            .onSuccess { changedMap ->
                                _state.update { it.copy(isLoading = false) }
                                Log.e("Sync", "Success")
                                if(changedMap.isNotEmpty()){
                                    _events.send(AuthEvent.SyncChange(changedMap))
                                }else{
                                    _state.update { it.copy(currentUser = user) }
                                    navigator.navigate(
                                        route =  Route.StudyTracker,
                                        navOptions = {
                                            popUpTo(Route.Authentication) {
                                                inclusive = true
                                            }
                                        }
                                    )
                                }
                            }
                            .onError { error ->
                                _state.update { it.copy(isLoading = false) }
                                _events.send(AuthEvent.SyncError(error))
                                Log.e("SyncError", error.message)
                            }
                    }
                    .onError { error ->
                        _state.update { it.copy(isLoading = false) }
                        _events.send(AuthEvent.AuthenticationError(error))
                    }
            }else{
                // Get login info from Data store
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            if (email.isBlank()) {
                _events.send(AuthEvent.AuthenticationError(AuthError.EMAIL_IS_BLANK))
                return@launch
            }
            if(password.isBlank()){
                _events.send(AuthEvent.AuthenticationError(AuthError.PASSWORD_IS_BLANK))
                return@launch
            }
            _state.update { it.copy(isLoading = true) }
            authRepository.register(email, password, displayName)
                .onSuccess {  user ->
                    if(state.value.hasLocalData){
                        authRepository.sendLocalDataToRemote()
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            currentUser = user
                        )
                    }
                    navigator.navigate(Route.LoginScreen)
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = "Register Successfully. Please login to continue.")
                    )
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(AuthEvent.AuthenticationError(error))
                }
        }
    }

}