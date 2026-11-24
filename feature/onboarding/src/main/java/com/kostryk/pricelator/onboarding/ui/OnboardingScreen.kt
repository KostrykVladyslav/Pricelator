package com.kostryk.pricelator.onboarding.ui

import androidx.compose.runtime.Composable

@Composable
fun OnboardingScreen(onOnboardingFinished: () -> Unit) {
    OnboardingContent(onOnboardingFinished = onOnboardingFinished)
}