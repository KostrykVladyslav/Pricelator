package com.kostryk.pricelator.onboarding.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kostryk.pricelator.core.ui.R

@Composable
fun OnboardingPage(
    title: String,
    description: String,
    image: Painter,
    onPreviousPressed: (() -> Unit)? = null,
    onNextPressed: (() -> Unit)? = null,
    isLastStep: Boolean = false
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(
            title = title,
            description = description,
            modifier = Modifier.padding(horizontal = 21.dp)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            OnboardingImage(image = image)
        }

        OnboardingNavigation(
            onPreviousPressed = onPreviousPressed,
            onNextPressed = onNextPressed,
            isLastStep = isLastStep,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
        )
    }
}

@Composable
private fun OnboardingHeader(title: String, description: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(top = 22.dp),
            text = title,
            fontSize = 32.sp,
            lineHeight = 42.sp,
            fontFamily = FontFamily(Font(R.font.hanken_grotesk_extrabold))
        )
        Text(
            modifier = Modifier.padding(top = 18.dp),
            text = description,
            fontSize = 24.sp,
            lineHeight = 31.sp,
            fontFamily = FontFamily(Font(R.font.hanken_grotesk))
        )
    }
}

@Composable
private fun OnboardingImage(image: Painter) {
    Image(
        modifier = Modifier.padding(top = 66.dp),
        painter = image,
        contentDescription = "compass",
        colorFilter = ColorFilter.tint(
            color = MaterialTheme.colorScheme.onBackground,
            blendMode = BlendMode.SrcAtop
        )
    )
}

@Composable
private fun OnboardingNavigation(
    onPreviousPressed: (() -> Unit)?,
    onNextPressed: (() -> Unit)?,
    isLastStep: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onPreviousPressed != null) {
            CircularIconButton(
                iconRes = R.drawable.ic_previous,
                contentDescription = "Previous",
                onClick = onPreviousPressed
            )
        } else {
            Spacer(modifier = Modifier.size(42.dp))
        }

        if (onNextPressed != null) {
            if (isLastStep) {
                ScanButton(onClick = onNextPressed)
            } else {
                CircularIconButton(
                    iconRes = R.drawable.ic_next,
                    contentDescription = "Next",
                    onClick = onNextPressed
                )
            }
        }
    }
}

@Composable
private fun CircularIconButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onBackground)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.background)
        )
    }
}

@Composable
private fun ScanButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(42.dp)
            .wrapContentWidth()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onBackground)
            .clickable(onClick = onClick)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 21.dp),
            text = stringResource(R.string.scan),
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.background,
            fontFamily = FontFamily(Font(R.font.hanken_grotesk_bold))
        )
    }
}