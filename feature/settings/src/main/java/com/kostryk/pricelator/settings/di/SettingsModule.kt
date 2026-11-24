package com.kostryk.pricelator.settings.di

import com.kostryk.pricelator.ui.navigation.NavigationFactory
import com.kostryk.pricelator.settings.api.SettingsFeatureApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SettingsModule {

    @Binds
    @IntoSet
    @Singleton
    fun bindSettingsFeatureApi(api: SettingsFeatureApi): NavigationFactory
}
