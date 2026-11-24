package com.kostryk.pricelator.onboarding.di

import com.kostryk.pricelator.camera.api.CameraFeatureApi
import com.kostryk.pricelator.domain.manager.preferences.SharedPreferencesManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DependencyProvider {
    val cameraFeatureApi: CameraFeatureApi
    val sharedPreferencesManager: SharedPreferencesManager
}