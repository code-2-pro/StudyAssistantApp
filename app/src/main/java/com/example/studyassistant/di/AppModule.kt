package com.example.studyassistant.di

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.studyassistant.core.data.networking.AndroidConnectivityObserver
import com.example.studyassistant.core.domain.ConnectivityObserver
import com.example.studyassistant.core.navigation.DefaultNavigator
import com.example.studyassistant.core.navigation.Navigator
import com.example.studyassistant.core.navigation.Route
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Singleton scope
object AppModule {

    val Context.dataStore by preferencesDataStore(name = "user_datastore")

    @Provides
    @Singleton
    fun provideNavigator(): Navigator {
        return DefaultNavigator(startDestination = Route.Authentication)
    }

    @Provides
    @Singleton
    fun provideConnectivityObserver(
        application: Application
    ): ConnectivityObserver {
        return AndroidConnectivityObserver(context = application)
    }

    @Provides
    @Singleton
    fun provideDataStore(context: Context) = context.dataStore

}