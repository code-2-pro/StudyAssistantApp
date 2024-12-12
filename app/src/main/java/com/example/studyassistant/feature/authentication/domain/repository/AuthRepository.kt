package com.example.studyassistant.feature.authentication.domain.repository

import com.example.studyassistant.core.domain.util.AuthError
import com.example.studyassistant.core.domain.util.RemoteDbError
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.feature.authentication.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun getCurrentUser(): User?

    suspend fun login(email: String, password: String): Result<User, AuthError>

    suspend fun register(email: String, password: String, displayName: String): Result<User, AuthError>

    suspend fun logout()

    suspend fun catchRealtimeUpdates()

    suspend fun getRemoteDataForLocal(): Result<Unit, RemoteDbError>

    suspend fun sendLocalDataToRemote(): Result<Unit, RemoteDbError>

    suspend fun checkDataConsistency(): Result<Map<String, Int>, RemoteDbError>

    fun checkHasLocalData(): Flow<Boolean>

    suspend fun removeAllLocalData()


}