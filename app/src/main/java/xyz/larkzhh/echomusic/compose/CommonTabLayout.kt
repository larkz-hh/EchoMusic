package xyz.larkzhh.echomusic.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp


@Composable
fun CommonTabLayout(
    selectedIndex: Int = 0,
    tabTexts: List<String>,
    backgroundColor: Color = AppColorsProvider.current.background,
    selectedTextColor: Color = AppColorsProvider.current.firstText,
    unselectedTextColor: Color = AppColorsProvider.current.secondText,
    indicatorColor: Brush = Brush.horizontalGradient(listOf(AppColorsProvider.current.primary, AppColorsProvider.current.secondary)),
    style: CommonTabLayoutStyle = CommonTabLayoutStyle(),
    onTabSelected: ((index: Int) -> Unit)? = null
) {
    if (style.isScrollable) {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            modifier = style.modifier,
            edgePadding = 0.dp,
            containerColor = backgroundColor,
            indicator = @Composable { tabPositions ->
                if (style.customIndicator != null) {
                    style.customIndicator.invoke(tabPositions[selectedIndex], selectedIndex)
                } else {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedIndex])
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        HorizontalDivider(
                            modifier = Modifier
                                .width(style.indicatorWidth)
                                .padding(bottom = style.indicatorPaddingBottom)
                                .background(
                                    brush = indicatorColor,
                                    shape = RoundedCornerShape(50)
                                ),
                            thickness = style.indicatorHeight,
                            color = Color.Transparent
                        )
                    }
                }
            },
            divider = @Composable {
                HorizontalDivider(color = Color.Transparent)
            }
        ) {
            tabTexts.forEachIndexed { i, tabText ->
                TabItem(
                    tabText = tabText,
                    isSelected = selectedIndex == i,
                    style = style,
                    selectedTextColor = selectedTextColor,
                    unselectedTextColor = unselectedTextColor,
                    index = i,
                    onTabSelected = onTabSelected
                )
            }
        }
    } else {
        TabRow(
            selectedTabIndex = selectedIndex,
            modifier = style.modifier,
            containerColor = backgroundColor,
            indicator = @Composable { tabPositions ->
                if (style.customIndicator != null) {
                    style.customIndicator.invoke(tabPositions[selectedIndex], selectedIndex)
                } else {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedIndex])
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        HorizontalDivider(
                            modifier = Modifier
                                .width(style.indicatorWidth)
                                .padding(bottom = style.indicatorPaddingBottom)
                                .background(
                                    brush = indicatorColor,
                                    shape = RoundedCornerShape(50)
                                ),
                            thickness = style.indicatorHeight,
                            color = Color.Transparent
                        )
                    }
                }
            },
            divider = @Composable {
                HorizontalDivider(color = Color.Transparent)
            }
        ) {
            tabTexts.forEachIndexed { i, tabText ->
                TabItem(
                    tabText = tabText,
                    isSelected = selectedIndex == i,
                    style = style,
                    selectedTextColor = selectedTextColor,
                    unselectedTextColor = unselectedTextColor,
                    index = i,
                    onTabSelected = onTabSelected
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    tabText: String,
    isSelected: Boolean,
    style: CommonTabLayoutStyle,
    selectedTextColor: Color,
    unselectedTextColor: Color,
    index: Int,
    onTabSelected: ((index: Int) -> Unit)?
) {
    val fontWeight = when {
        isSelected && style.selectedTextBold -> FontWeight.Bold
        !isSelected && style.unselectedTextBold -> FontWeight.Bold
        else -> FontWeight.Normal
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onTabSelected?.invoke(index)
                    }
                )
            }
            .zIndex(1f)
            .drawBehind {
                style.tabItemDrawBehindBlock?.invoke(this, index)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tabText,
            fontSize = if (isSelected) style.selectedTextSize else style.unselectedTextSize,
            fontWeight = fontWeight,
            color = if (isSelected) selectedTextColor else unselectedTextColor,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * 通用TabBar样式
 */
data class CommonTabLayoutStyle(
    val modifier: Modifier = Modifier,
    val selectedTextSize: TextUnit = 36.csp,
    val unselectedTextSize: TextUnit = 36.csp,
    val selectedTextBold: Boolean = true,
    val unselectedTextBold: Boolean = false,
    val indicatorWidth: Dp = 140.cdp,
    val indicatorHeight: Dp = 12.cdp,
    val indicatorPaddingBottom: Dp = 0.cdp,
    val isScrollable: Boolean = true,
    val tabItemDrawBehindBlock: (DrawScope.(position: Int) -> Unit)? = null,
    val customIndicator: @Composable ((selectedTabPosition: TabPosition, selectedPosition: Int) -> Unit)? = null
)
