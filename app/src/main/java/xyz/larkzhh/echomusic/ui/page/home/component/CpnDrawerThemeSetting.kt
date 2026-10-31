package xyz.larkzhh.echomusic.ui.page.home.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.ui.theme.*
import xyz.larkzhh.echomusic.ui.theme.color.palette.dark.DarkColorPalette
import xyz.larkzhh.echomusic.ui.theme.color.palette.light.*
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import xyz.larkzhh.echomusic.utils.onClick
import kotlinx.coroutines.launch

@Composable
fun CpnDrawerThemeSetting() {
    val anim by remember {
        mutableStateOf(Animatable(0f))
    }
    val themeListHeight = 88 * themeModels.size
    val scope = rememberCoroutineScope()
    val currentTheme = themeModels.firstOrNull { it.themeType == themeTypeState.value }
        ?: themeModels.first()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height((anim.value * themeListHeight + 88f).cdp)
    ) {
        CpnDrawerItem(
            R.drawable.ic_theme,
            R.drawable.ic_arrow_down,
            rightIconModifier = Modifier.graphicsLayer {
                rotationZ = anim.value * 180
            },
            title = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = AppColorsProvider.current.firstText,
                        fontSize = 32.csp
                    )
                ) {
                    append("当前主题")
                }
                withStyle(
                    style = SpanStyle(
                        color = AppColorsProvider.current.secondText,
                        fontSize = 28.csp
                    )
                ) {
                    append("（${currentTheme.name}）")
                }
            },
        ) {
            showThemeList = !showThemeList
            scope.launch {
                if (showThemeList) {
                    anim.animateTo(1f, tween(500))
                } else {
                    anim.animateTo(0f, tween(500))
                }
            }
        }
        themeModels.forEach { themeModel ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.cdp)
                    .onClick { setThemeType(themeModel.themeType) }
                    .padding(horizontal = 32.cdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.cdp)
                        .clip(RoundedCornerShape(50))
                        .background(themeModel.color)
                )

                Text(
                    text = themeModel.name,
                    modifier = Modifier.padding(start = 24.cdp),
                    fontSize = 32.csp,
                    color = AppColorsProvider.current.secondText
                )

                Spacer(modifier = Modifier.weight(1f))

                if (themeModel.themeType == themeTypeState.value) {
                    CommonIcon(
                        resId = R.drawable.ic_checked,
                        modifier = Modifier.size(32.cdp),
                        tint = themeModel.color
                    )
                }
            }
        }
    }
}

private var showThemeList by mutableStateOf(false)

private val themeModels = listOf(
    ThemeModel("默认", THEME_DEFAULT, DefaultColorPalette.primary),
    ThemeModel("夜间", THEME_NIGHT, DarkColorPalette.pure),
    ThemeModel("蓝色", THEME_BLUE, BlueColorPalette.primary),
    ThemeModel("绿色", THEME_GREEN, GreenColorPalette.primary),
    ThemeModel("橙色", THEME_ORIGIN, OriginColorPalette.primary),
    ThemeModel("紫色", THEME_PURPLE, PurpleColorPalette.primary),
    ThemeModel("黄色", THEME_YELLOW, YellowColorPalette.primary),
)

data class ThemeModel(val name: String, val themeType: Int, val color: Color)
