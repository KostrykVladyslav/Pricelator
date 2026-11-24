package com.kostryk.pricelator.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kostryk.pricelator.domain.model.CurrencyEntity
import com.kostryk.pricelator.domain.util.CurrencyUtils
import kotlinx.coroutines.delay
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurrencyWheelPicker(
    items: List<CurrencyEntity>,
    visibleItemsCount: Int = 5,
    itemHeight: Int = 46,
    selectedIndex: Int = 0,
    onItemSnapped: (CurrencyEntity) -> Unit
) {
    val localConfiguration = LocalConfiguration.current
    val maxTextWidth = remember { localConfiguration.screenWidthDp.dp / 2 }

    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val itemHeightDp = remember { itemHeight.dp }

    val contentPadding = (visibleItemsCount / 2) * itemHeight

    LaunchedEffect(selectedIndex) {
        listState.scrollToItem(selectedIndex)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((visibleItemsCount * itemHeight).dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f) // Width of the gray bar
                .height(itemHeightDp)
                .background(
                    color = Color.LightGray.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)
                )
        )

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = contentPadding.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            items(items.size) { index ->
                val item = items[index]

                val layoutInfo = remember { derivedStateOf { listState.layoutInfo } }
                val visibleItemsInfo = layoutInfo.value.visibleItemsInfo
                val itemInfo = visibleItemsInfo.find { it.index == index }

                var scale by remember { mutableFloatStateOf(1f) }
                var alpha by remember { mutableFloatStateOf(1f) }
                var isSelected by remember { mutableStateOf(false) }

                if (itemInfo != null) {
                    val containerCenter =
                        layoutInfo.value.viewportStartOffset + (layoutInfo.value.viewportEndOffset - layoutInfo.value.viewportStartOffset) / 2f
                    val itemCenter = itemInfo.offset + itemInfo.size / 2f
                    val distanceFromCenter = abs(containerCenter - itemCenter)

                    val maxDistance = itemHeight * 2.5f
                    val scalingFactor = (1f - (distanceFromCenter / maxDistance)).coerceIn(0.5f, 1f)

                    scale = scalingFactor
                    alpha = scalingFactor
                    isSelected = distanceFromCenter < (itemHeight / 2) // Roughly center

                    if (isSelected) {
                        LaunchedEffect(item) {
                            delay(500)
                            onItemSnapped(item)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .height(itemHeightDp)
                        .fillMaxWidth()
                        .graphicsLayer {
                            this.scaleX = scale
                            this.scaleY = scale
                            this.alpha = alpha
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = CurrencyUtils.getCountryFlag(item.symbol),
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = CurrencyUtils.formatCurrencyName(item.symbol),
                        fontSize = 22.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onBackground else Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .widthIn(max = maxTextWidth)
                            .basicMarquee()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPicker() {
    val currencies = listOf<CurrencyEntity>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.Center
    ) {
        CurrencyWheelPicker(
            items = currencies,
            selectedIndex = 3,
            onItemSnapped = { selected ->
                println("Selected: ${selected.name}")
            })
    }
}
