package com.chelios.lukabook.di

import com.chelios.lukabook.repositories.AuthRepository
import com.chelios.lukabook.repositories.DefaultAuthRepository
import com.chelios.lukabook.repositories.DefaultMainRepository
import com.chelios.lukabook.repositories.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object MainModule {

    @ActivityScoped
    @Provides
    fun provideMainRepository() = DefaultMainRepository() as MainRepository
}