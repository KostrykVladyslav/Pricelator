package com.kostryk.pricelator.camera.ui.elements

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kostryk.pricelator.core.ui.R
import com.kostryk.pricelator.domain.model.CurrencyEntity
import com.kostryk.pricelator.domain.util.CurrencyUtils
import com.kostryk.pricelator.ui.theme.AppFont
import java.text.DecimalFormat

@Composable
fun ConversionResultCard(
    detectedAmount: Double,
    convertedAmount: Double,
    sourceCurrency: CurrencyEntity,
    targetCurrency: CurrencyEntity,
    isFlashlightOn: Boolean,
    updateDate: String,
    onSourceCurrencyClick: () -> Unit,
    onTargetCurrencyClick: () -> Unit,
    toggleFlashlight: () -> Unit,
    modifier: Modifier = Modifier
) {
    val decimalFormat = DecimalFormat("#,##0.00")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedGradientBox(
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    var fontSize by remember { mutableStateOf(16.sp) }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimatedContent(
                            targetState = "${decimalFormat.format(detectedAmount)} ${sourceCurrency.symbol} = ${
                                decimalFormat.format(convertedAmount)
                            } ${targetCurrency.symbol}",
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) + slideInVertically(
                                    animationSpec = tween(300),
                                    initialOffsetY = { it / 4 }
                                ) togetherWith fadeOut(animationSpec = tween(300)) + slideOutVertically(
                                    animationSpec = tween(300),
                                    targetOffsetY = { -it / 4 }
                                )
                            },
                            label = "currency_conversion_animation"
                        ) { text ->
                            Text(
                                text = text,
                                fontSize = fontSize,
                                fontFamily = AppFont,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                softWrap = true,
                                color = Color.Black,
                                onTextLayout = { textLayoutResult ->
                                    if (textLayoutResult.hasVisualOverflow && fontSize.value > 12f) {
                                        fontSize = (fontSize.value - 1).sp
                                    }
                                }
                            )
                        }
                    }
                }

                Image(
                    painter = painterResource(if (isFlashlightOn) R.drawable.ic_light_on else R.drawable.ic_light_off),
                    contentDescription = "light",
                    modifier = modifier
                        .clip(CircleShape)
                        .align(Alignment.CenterEnd)
                        .clickable(onClick = toggleFlashlight)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Exchange rate info
            Text(
                text = "1 ${sourceCurrency.symbol} = ${decimalFormat.format(targetCurrency.exchangeRate / sourceCurrency.exchangeRate)} ${targetCurrency.symbol}",
                fontSize = 14.sp,
                fontFamily = AppFont,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.update_on) + " " + updateDate,
                fontSize = 12.sp,
                fontFamily = AppFont,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Source currency selector
                CurrencyButton(
                    currency = sourceCurrency,
                    onClick = onSourceCurrencyClick
                )

                // Arrow icon
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "to",
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    modifier = Modifier.size(24.dp)
                )

                // Target currency selector
                CurrencyButton(
                    currency = targetCurrency,
                    onClick = onTargetCurrencyClick
                )
            }
        }
    }
}

@Composable
private fun CurrencyButton(
    currency: CurrencyEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundShape = RoundedCornerShape(32.dp)
    Row(
        modifier = modifier
            .shadow(elevation = 6.dp, backgroundShape)
            .clip(backgroundShape)
            .background(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
            .padding(start = 32.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${CurrencyUtils.getCountryFlag(currency.symbol)} ${currency.symbol}",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_down),
            contentDescription = "selectorImage",
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
        )
    }
}

@Composable
@Preview
private fun Preview() {
    ConversionResultCard(
        detectedAmount = 20.0,
        convertedAmount = 300000.0,
        sourceCurrency = CurrencyEntity(0, "Euro", "UAH", 1.45),
        targetCurrency = CurrencyEntity(0, "USA Dollar", "JPY", 1.00),
        isFlashlightOn = false,
        updateDate = "24-11-2025",
        onSourceCurrencyClick = {},
        onTargetCurrencyClick = {},
        toggleFlashlight = {}
    )
}