package com.example.studyassistant.di

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

    @Provides
    @Singleton
    fun provideNavigator(): Navigator {
        return DefaultNavigator(startDestination = Route.DashboardScreen)
    }
}