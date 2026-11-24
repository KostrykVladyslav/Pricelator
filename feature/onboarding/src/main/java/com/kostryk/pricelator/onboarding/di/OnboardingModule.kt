package com.kostryk.pricelator.onboarding.di

import android.content.Context
import com.kostryk.pricelator.ui.navigation.NavigationFactory
import com.kostryk.pricelator.onboarding.api.OnboardingFeatureApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
class OnboardingModule {

    @Provides
    fun provideHomeFeatureApi(@ApplicationContext context: Context): OnboardingFeatureApi {
        return OnboardingFeatureApi(context)
    }

    @Provides
    @IntoSet
    fun provideHomeNavigationFactory(@ApplicationContext context: Context): NavigationFactory {
        return OnboardingFeatureApi(context)
    }
}