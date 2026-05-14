package xyz.larkzhh.echomusic.ui.page.mine.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.compose.TableLayout
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp


@Composable
fun CpnMusicApplication() {
    val musicApplications = remember {
        listOf(
            MusicApplicationBean(R.drawable.ic_music, "本地/下载"),
            MusicApplicationBean(R.drawable.ic_cloud_disk, "云盘"),
            MusicApplicationBean(R.drawable.ic_buy, "已购"),
            MusicApplicationBean(R.drawable.ic_recent_play, "最近播放"),
            MusicApplicationBean(R.drawable.ic_friends, "我的好友"),
            MusicApplicationBean(R.drawable.ic_collect, "收藏和赞"),
            MusicApplicationBean(R.drawable.ic_podcast, "我的播客"),
            MusicApplicationBean(R.drawable.ic_rock, "摇滚专区")
        )
    }

    TableLayout(cellsCount = 4) {
        musicApplications.forEachIndexed { _, item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable {
                        when (item.name) {
                            "最近播放" -> EchoNavController.instance.navigate(RouterUrls.RECENT_PLAY)
                        }
                    }
            ) {
                Icon(
                    painterResource(item.imageResId),
                    null,
                    tint = AppColorsProvider.current.primary,
                    modifier = Modifier
                        .padding(top = 20.cdp)
                        .size(60.cdp)
                )
                Text(
                    text = item.name,
                    modifier = Modifier.padding(vertical = 20.cdp),
                    color = AppColorsProvider.current.secondText,
                    fontSize = 24.csp
                )
            }
        }
    }
}

data class MusicApplicationBean(val imageResId: Int, val name: String)
