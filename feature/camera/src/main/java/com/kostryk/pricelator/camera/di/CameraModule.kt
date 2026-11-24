package com.kostryk.pricelator.camera.di

import com.kostryk.pricelator.camera.api.CameraFeatureApi
import com.kostryk.pricelator.ui.navigation.NavigationFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
class CameraModule {

    @Provides
    fun provideCameraFeatureApi(): CameraFeatureApi {
        return CameraFeatureApi()
    }

    @Provides
    @IntoSet
    fun provideCameraNavigationFactory(): NavigationFactory {
        return CameraFeatureApi()
    }
}