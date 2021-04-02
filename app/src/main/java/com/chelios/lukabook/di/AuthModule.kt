package com.chelios.lukabook.di

import com.chelios.lukabook.repositories.AuthRepository
import com.chelios.lukabook.repositories.DefaultAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object AuthModule {

    @ActivityScoped
    @Provides
    fun provideauthRepository() = DefaultAuthRepository() as AuthRepository
}