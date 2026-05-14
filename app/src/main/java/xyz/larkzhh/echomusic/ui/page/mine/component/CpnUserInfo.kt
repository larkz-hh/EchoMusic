package xyz.larkzhh.echomusic.ui.page.mine.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import xyz.larkzhh.echomusic.core.AppGlobalData
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import xyz.larkzhh.echomusic.R

@Composable
fun CpnUserInfo(modifier: Modifier = Modifier) {
    val loginResult = AppGlobalData.sLoginResult
    if (loginResult == null) {
        GuestUserInfo(modifier)
        return
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(top = 60.cdp, start = 32.cdp, end = 32.cdp)
                .fillMaxWidth()
                .height(240.cdp)
                .clip(RoundedCornerShape(24.cdp))
                .background(AppColorsProvider.current.card),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = loginResult.profile.nickname,
                fontSize = 40.csp,
                color = AppColorsProvider.current.firstText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 64.cdp)
            )
            Text(
                text = "${loginResult.profile.follows} 关注  " +
                        "｜  ${loginResult.profile.followeds} 粉丝",
                fontSize = 32.csp,
                color = AppColorsProvider.current.secondText,
                modifier = Modifier.padding(top = 36.cdp)
            )
        }

        CommonNetworkImage(
            url = loginResult.profile.avatarUrl,
            placeholder = R.drawable.ic_default_avator,
            error = R.drawable.ic_default_avator,
            modifier = Modifier.size(120.cdp)
                .clip(
                    RoundedCornerShape(50)
                )
        )
    }
}

/** 游客用户卡片 */
@Composable
private fun GuestUserInfo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(top = 60.cdp, start = 32.cdp, end = 32.cdp)
                .fillMaxWidth()
                .height(240.cdp)
                .clip(RoundedCornerShape(24.cdp))
                .background(AppColorsProvider.current.card)
                .clickable {
                    EchoNavController.instance.navigate(RouterUrls.LOGIN)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "未登录",
                fontSize = 40.csp,
                color = AppColorsProvider.current.firstText,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "点击扫码登录，同步你的歌单",
                fontSize = 28.csp,
                color = AppColorsProvider.current.secondText,
                modifier = Modifier.padding(top = 20.cdp)
            )
        }
    }
}
