package com.kostryk.pricelator.onboarding.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kostryk.pricelator.core.ui.R
import com.kostryk.pricelator.ui.theme.AppTheme
import com.kostryk.pricelator.ui.theme.isDarkTheme
import com.kostryk.pricelator.onboarding.ui.pages.OnboardingPage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingContent(onOnboardingFinished: () -> Unit) {

    val pagerState = rememberPagerState { 3 }
    val coroutineScope = rememberCoroutineScope()

    Scaffold { contentPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                when (page) {
                    0 -> OnboardingPage(
                        title = stringResource(R.string.onboarding_first_step_title),
                        description = stringResource(R.string.onboarding_first_step_description),
                        image = painterResource(R.drawable.ic_compass),
                        onPreviousPressed = null,
                        onNextPressed = {
                            coroutineScope.launch { pagerState.animateScrollToPage(page + 1) }
                        },
                    )

                    1 -> OnboardingPage(
                        title = stringResource(R.string.onboarding_second_step_title),
                        description = stringResource(R.string.onboarding_second_step_description),
                        image = painterResource(R.drawable.ic_camera),
                        onPreviousPressed = {
                            coroutineScope.launch { pagerState.animateScrollToPage(page - 1) }
                        },
                        onNextPressed = {
                            coroutineScope.launch { pagerState.animateScrollToPage(page + 1) }
                        },
                    )

                    2 -> OnboardingPage(
                        title = stringResource(R.string.onboarding_third_step_title),
                        description = stringResource(R.string.onboarding_third_step_description),
                        image = painterResource(R.drawable.ic_dollar),
                        onPreviousPressed = {
                            coroutineScope.launch { pagerState.animateScrollToPage(page - 1) }
                        },
                        onNextPressed = onOnboardingFinished,
                        isLastStep = true
                    )

                    else -> error("Invalid page index: $page")
                }
            }

            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val isActive = pagerState.currentPage == iteration

                    val color by animateColorAsState(
                        targetValue = if (isActive) {
                            if (isDarkTheme()) {
                                Color.White
                            } else Color.Black
                        } else if (isDarkTheme()) {
                            colorResource(R.color.gray_opacity_20)
                        } else colorResource(R.color.gray),
                        animationSpec = tween(durationMillis = 300),
                        label = "indicator_color"
                    )

                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(CircleShape)
                            .background(color)
                            .alpha(if (isActive) 1f else 0.6f)
                            .size(10.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun OnboardingContentPreview() {
    AppTheme { OnboardingContent {} }
}