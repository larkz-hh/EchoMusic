package xyz.larkzhh.echomusic.ui.page.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import xyz.larkzhh.echomusic.core.AppGlobalData
import xyz.larkzhh.echomusic.compose.CommonHeadBackgroundShape
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.ui.page.mine.component.CpnUserInfo
import xyz.larkzhh.echomusic.ui.page.mine.mineCommonCard
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp

@Composable
fun ProfilePage() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.background)
    ) {

        CommonNetworkImage(
            url = AppGlobalData.sLoginResult?.profile?.backgroundUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(584.cdp)
                .clip(CommonHeadBackgroundShape()),
            error = R.drawable.ic_bg,
            contentScale = ContentScale.FillBounds
        )

        Column {
            // 用户信息
            CpnUserInfo(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 88.cdp)
                    .padding(top = 280.cdp)
            )

            Box(
                modifier = Modifier
                    .mineCommonCard()
                    .height(400.cdp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "个人详情页", fontSize = 50.csp, fontWeight = FontWeight.Bold)
            }
        }

    }

}