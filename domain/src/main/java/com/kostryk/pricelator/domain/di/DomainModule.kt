package com.kostryk.pricelator.domain.di

import com.kostryk.pricelator.domain.AppDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class DomainModule {
    @Provides
    fun provideAppDispatchers(): AppDispatcher {
        return AppDispatcher()
    }
}