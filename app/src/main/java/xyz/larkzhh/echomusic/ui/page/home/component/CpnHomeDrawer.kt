package xyz.larkzhh.echomusic.ui.page.home.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import xyz.larkzhh.echomusic.core.AppGlobalData
import xyz.larkzhh.echomusic.core.audio.EqController
import xyz.larkzhh.echomusic.ui.page.playmusic.component.EqSheet
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.onClick
import kotlinx.coroutines.launch
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.utils.csp

@Composable
fun CpnHomeDrawer(drawerState: DrawerState) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.background),
    ) {
        UserInfoComponent(drawerState)
        SettingComponent()
    }
}

@Composable
private fun UserInfoComponent(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    val loginResult = AppGlobalData.sLoginResult
    Log.e("cloudMusic", "AppGlobalData.sLoginRefreshFlag=${AppGlobalData.sLoginRefreshFlag}")
    if(loginResult != null) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 24.cdp)
                .fillMaxWidth()
                .height(100.cdp)
                .onClick {
                    scope.launch {
                        drawerState.close()
                        EchoNavController.instance.navigate(RouterUrls.PROFILE)
                    }
                }
                .padding(horizontal = 32.cdp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CommonNetworkImage(
                url = loginResult.profile.avatarUrl,
                placeholder = R.drawable.ic_default_avator,
                error = R.drawable.ic_default_avator,
                modifier = Modifier.size(60.cdp)
                    .clip(
                        RoundedCornerShape(60)
                    )
            )
            Text(
                text = loginResult.profile.nickname,
                fontSize = 36.csp,
                color = AppColorsProvider.current.firstText,
                modifier = Modifier.padding(start = 20.cdp)
            )

            CommonIcon(
                resId = R.drawable.ic_arrow_right,
                modifier = Modifier.padding(8.cdp)
                    .size(30.cdp)
            )
        }
    } else {
        // 游客登录入口
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 24.cdp)
                .fillMaxWidth()
                .height(100.cdp)
                .onClick {
                    scope.launch {
                        drawerState.close()
                        EchoNavController.instance.navigate(RouterUrls.LOGIN)
                    }
                }
                .padding(horizontal = 32.cdp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CommonNetworkImage(
                url = null,
                placeholder = R.drawable.ic_default_avator,
                error = R.drawable.ic_default_avator,
                modifier = Modifier.size(60.cdp)
                    .clip(RoundedCornerShape(60))
            )
            Text(
                text = "点击扫码登录",
                fontSize = 36.csp,
                color = AppColorsProvider.current.firstText,
                modifier = Modifier.padding(start = 20.cdp)
            )
        }
    }

}

@Composable
private fun SettingComponent() {
    Column(
        modifier = Modifier
            .padding(horizontal = 32.cdp, vertical = 16.cdp)
            .background(AppColorsProvider.current.card, RoundedCornerShape(24.cdp))
    ) {
        Text(
            text = "设置",
            modifier = Modifier.padding(horizontal = 32.cdp, vertical = 24.cdp),
            fontSize = 28.csp,
            color = AppColorsProvider.current.secondText
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.cdp,
            color = AppColorsProvider.current.divider
        )

        CpnDrawerThemeSetting()

        var showEqSheet by remember { mutableStateOf(false) }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.cdp,
            color = AppColorsProvider.current.divider
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showEqSheet = true }
                .padding(horizontal = 32.cdp, vertical = 24.cdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "音效",
                fontSize = 28.csp,
                color = AppColorsProvider.current.secondText
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = EqController.preset.title,
                fontSize = 26.csp,
                color = AppColorsProvider.current.thirdText
            )
        }

        if (showEqSheet) {
            EqSheet(onDismiss = { showEqSheet = false })
        }

        if (AppGlobalData.sLoginResult != null) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.cdp,
                color = AppColorsProvider.current.divider
            )
            Text(
                text = "退出登录",
                modifier = Modifier
                    .fillMaxWidth()
                    .onClick {
                        AppGlobalData.logout()
                        EchoNavController.instance.navigate(RouterUrls.LOGIN)
                    }
                    .padding(horizontal = 32.cdp, vertical = 24.cdp),
                fontSize = 28.csp,
                color = AppColorsProvider.current.secondText
            )
        }
    }
}
