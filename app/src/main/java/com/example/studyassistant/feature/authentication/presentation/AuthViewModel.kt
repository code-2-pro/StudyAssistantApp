package com.example.studyassistant.feature.authentication.presentation

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    connectivityObserver: ConnectivityObserver,
    private val dataStore: DataStore<Preferences>,
    private val navigator: Navigator
): ViewModel() {

    // Define the preferences keys
    private val USER_DARK_THEME_KEY = booleanPreferencesKey("user_dark_theme")

    private val _state = MutableStateFlow(AuthState())
    val state = combine(
        _state,
        authRepository.checkHasLocalData(),
    ){ state, hasLocalData->
        state.copy(hasLocalData = hasLocalData)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthState()
    )

    val isOnline: StateFlow<Boolean> = connectivityObserver.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )


    private val _events = Channel<AuthEvent>()
    val events = _events.receiveAsFlow()


    init {
        viewModelScope.launch {
            delay(1000L) // Delay by 1 seconds to allow isOnline to stabilize
            val isOnlineNow = isOnline.first() // Collect the first value after the delay
            checkAuthStatus(isOnlineNow)
        }
    }

    suspend fun checkAuthStatus(isOnline: Boolean) {
        _state.update { it.copy(isLoading = true) }
        val user = authRepository.getCurrentUser()
        Log.e("isOnline", isOnline.toString())

        if (user != null) {
            _state.update { it.copy(currentUser = user) }

            if (isOnline) {
                authRepository.checkDataConsistency()
                    .onSuccess { changedMap ->
                        _state.update { it.copy(isLoading = false) }
                        Log.e("Sync", "Success")
                        if (changedMap.isNotEmpty()) {
                            _events.send(AuthEvent.SyncChange(changedMap))
                        } else {
                            Log.e("Sync", "Reach No Changed")
                            navigator.navigate(
                                route = Route.StudyTracker,
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
            } else {
                navigator.navigate(
                    route = Route.StudyTracker,
                    navOptions = {
                        popUpTo(Route.Authentication) {
                            inclusive = true
                        }
                    }
                )
            }
        } else {
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun catchRealtimeUpdate(){
        viewModelScope.launch{
            _state.update { it.copy(isLoading = true) }
            if(isOnline.value){
                authRepository.catchRealtimeUpdates()
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onAction(action: AuthAction){
        when(action){
            is AuthAction.Login -> login(action.email, action.password)
            is AuthAction.Register -> {
                register(action.email, action.password, action.displayName)
            }
            is AuthAction.UpdateUserInfo -> {
                updateUserInfo(
                    action.currentPassword,
                    action.newDisplayName,
                    action.newEmail,
                    action.newPassword
                )
            }
            AuthAction.GoToLoginPage -> goToLoginPage()
            AuthAction.GoToRegisterPage -> goToRegisterPage()
            AuthAction.GetDataFromRemote -> getDataFromRemote()
            AuthAction.SendDataToRemote ->  sendDataToRemote()
            AuthAction.UseNoAccount -> useNoAccount()
            AuthAction.LogoutKeepLocalData -> logoutWithKeepLocalData()
            AuthAction.LogoutRemoveLocalData -> logoutWithRemoveLocalData()
            AuthAction.DismissSync -> dismissSync()
            is AuthAction.ToggleDarkTheme -> saveDarkThemeOption(action.isEnabled)
        }
    }


    // Write data to DataStore
    private fun saveDarkThemeOption(isEnabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[USER_DARK_THEME_KEY] = isEnabled
            }
        }
    }

    // Clears all keys and their values from DataStore
    private fun clearAllDataStore() {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences.clear()
                }
            } catch (e: Exception) {
                Log.e("DataStore", "Error clearing DataStore", e)
            }
        }
    }


    private fun dismissSync(){
        viewModelScope.launch{
            _state.update { it.copy(
                currentUser = null,
            ) }
            authRepository.logout()
        }
    }

    private fun updateUserInfo(
        currentPassword: String,
        newDisplayName: String,
        newEmail: String,
        newPassword: String
    ) {
        viewModelScope.launch {
            val currentUser = state.value.currentUser
            _state.update { it.copy(isLoading = true) }
            if (currentUser != null) {
                authRepository.updateUserInfo(
                    currentPassword = currentPassword,
                    newDisplayName = newDisplayName,
                    newEmail = newEmail,
                    newPassword = newPassword
                )
                    .onSuccess {
                        _state.update { it.copy(
                            currentUser = authRepository.getCurrentUser(),
                            isLoading = false
                        ) }
                        navigator.navigateUp()
                        SnackbarController.sendEvent(
                            SnackbarEvent(message = "Update Info Successfully")
                        )
                    }
                    .onError { error ->
                        _state.update { it.copy(isLoading = false) }
                        _events.send(AuthEvent.AuthenticationError(error))
                    }
            } else {
                // Handle case where no current user is found
                _state.update { it.copy(isLoading = false) }
                _events.send(AuthEvent.AuthenticationError(AuthError.USER_NOT_FOUND))
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
            authRepository.login(email, password)
                .onSuccess {  user ->
                    authRepository.checkDataConsistency()
                        .onSuccess { changedMap ->
                            _state.update { it.copy(
                                isLoading = false,
                                currentUser = user
                            ) }
                            Log.e("Sync", "Success")
                            if(changedMap.isNotEmpty()){
                                _events.send(AuthEvent.SyncChange(changedMap))
                            }else{
                                Log.e("Sync", "Reach No Changed")
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