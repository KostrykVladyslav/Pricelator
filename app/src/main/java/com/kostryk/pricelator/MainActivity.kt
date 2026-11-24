package com.kostryk.pricelator

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.kostryk.pricelator.camera.api.CameraFeatureApi
import com.kostryk.pricelator.domain.manager.language.LanguageManager
import com.kostryk.pricelator.domain.manager.preferences.SharedPreferencesManager
import com.kostryk.pricelator.domain.manager.theme.ThemeManager
import com.kostryk.pricelator.domain.model.Theme
import com.kostryk.pricelator.ui.navigation.NavigationFactory
import com.kostryk.pricelator.ui.theme.AppTheme
import com.kostryk.pricelator.onboarding.api.OnboardingFeatureApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationFactories: @JvmSuppressWildcards Set<NavigationFactory>

    @Inject
    lateinit var onboardingFeatureApi: OnboardingFeatureApi

    @Inject
    lateinit var cameraFeatureApi: CameraFeatureApi

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var languageManager: LanguageManager

    private var keepSplashScreen: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { keepSplashScreen }

        setContent {
            val currentTheme by themeManager.currentTheme.collectAsState()
            val currentLanguage by languageManager.currentLanguage.collectAsState()
            val systemInDarkTheme = isSystemInDarkTheme()

            LaunchedEffect(currentLanguage) {
                val locale = try {
                    Locale(currentLanguage.code)
                } catch (e: Exception) {
                    Locale.getDefault()
                }
                wrap(this@MainActivity, locale)
            }

            val darkTheme = when (currentTheme) {
                Theme.SYSTEM_DEFAULT -> systemInDarkTheme
                Theme.LIGHT -> false
                Theme.DARK -> true
            }

            AppTheme(darkTheme = darkTheme) {
                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        keepSplashScreen = false
                    }
                }

                enableEdgeToEdge()
                WindowCompat.setDecorFitsSystemWindows(window, false)

                val isFirstLaunch = sharedPreferencesManager
                    .get(SharedPreferencesManager.KEY_FIRST_LAUNCH, true)

                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(
                        navController = navController,
                        navigationFactories = navigationFactories,
                        startDestination = if (isFirstLaunch) {
                            onboardingFeatureApi.route
                        } else cameraFeatureApi.route,
                        modifier = Modifier.fillMaxSize()
                    )
                    it
                }
            }
        }
    }

    companion object {
        fun wrap(activity: Activity, newLocale: Locale): Context {
            val res = activity.resources
            Locale.setDefault(newLocale)

            val configuration = res.configuration
            configuration.setLocale(newLocale)
            configuration.setLayoutDirection(newLocale)
            activity.resources.updateConfiguration(configuration, res.displayMetrics)
            return activity.createConfigurationContext(configuration)
        }
    }
}