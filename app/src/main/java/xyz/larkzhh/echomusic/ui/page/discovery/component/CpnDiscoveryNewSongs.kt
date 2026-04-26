package xyz.larkzhh.echomusic.ui.page.discovery.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.model.Subscribers
import xyz.larkzhh.echomusic.model.Video
import com.google.gson.Gson
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import androidx.compose.foundation.layout.fillMaxHeight
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp

@Composable
fun CpnDiscoveryNewSongs(
    title: String = "新歌新碟",
    songs: List<DiscoveryNewSongItem> = DEFAULT_NEW_SONGS,
    pageSize: Int = 3,
) {
    val safeSongs = songs
    if (safeSongs.isEmpty()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.cdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = title,
                    color = AppColorsProvider.current.firstText,
                    fontSize = 32.csp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.width(10.cdp))
                Text(
                    text = "›",
                    color = AppColorsProvider.current.secondText,
                    fontSize = 28.csp,
                )
            }
        }
        return
    }
    val pageCount = remember(safeSongs.size) {
        (safeSongs.size + pageSize - 1) / pageSize
    }.coerceAtLeast(1)

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pageCount }
    )

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .width(maxWidth)
                .align(Alignment.Center)
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.cdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = title,
                color = AppColorsProvider.current.firstText,
                fontSize = 32.csp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(10.cdp))
            Text(
                text = "›",
                color = AppColorsProvider.current.secondText,
                fontSize = 28.csp,
            )
        }

        // 减少标题到列表的间距，让翻到右侧页时内容更靠上显示
        Spacer(modifier = Modifier.height(6.cdp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.cdp)
        ) { page ->
            val startIndex = page * pageSize
            val pageItems = safeSongs.drop(startIndex).take(pageSize)
            Column(verticalArrangement = Arrangement.spacedBy(16.cdp)) {
                pageItems.forEachIndexed { index, item ->
                    val globalIndex = startIndex + index
                    DiscoveryNewSongRow(
                        item = item,
                        qualityHint = QUALITY_HINTS.getOrNull(globalIndex % QUALITY_HINTS.size)
                    )
                }
            }
        }
        }
    }
}

private val QUALITY_HINTS = listOf("Hi-Res", "SQ", "SQ", null)

@Composable
private fun DiscoveryNewSongRow(
    item: DiscoveryNewSongItem,
    qualityHint: String?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.cdp)
            .clickable { openVideoPlayPage(item) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CommonNetworkImage(
                url = item.cover,
                modifier = Modifier
                    .size(70.cdp)
                    .clip(RoundedCornerShape(8.cdp)),
                placeholder = R.drawable.ic_default_placeholder_video,
                error = R.drawable.ic_default_placeholder_video
            )
            Spacer(modifier = Modifier.width(16.cdp))
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.name,
                    color = AppColorsProvider.current.firstText,
                    fontSize = 26.csp,
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.cdp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!qualityHint.isNullOrBlank()) {
                        Text(
                            text = qualityHint,
                            color = Color.White,
                            fontSize = 13.csp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .background(Color(0xFFE64B5B), RoundedCornerShape(6.cdp))
                                .padding(horizontal = 8.cdp, vertical = 3.cdp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.cdp))
                    }
                    Text(
                        text = item.artist,
                        color = AppColorsProvider.current.secondText,
                        fontSize = 22.csp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        CommonIcon(
            resId = R.drawable.ic_video_play,
            tint = AppColorsProvider.current.secondIcon,
            modifier = Modifier.size(26.cdp)
        )
    }
}

data class DiscoveryNewSongItem(
    val cover: String,
    val name: String,
    val artist: String,
    val mvid: Long?,
)

private val DEFAULT_NEW_SONGS: List<DiscoveryNewSongItem> = emptyList()

private fun openVideoPlayPage(item: DiscoveryNewSongItem) {
    val mvid = item.mvid ?: return
    // 这里先构造最小 Video 入参；页面进入后会通过 getMvDetailAndFill(item.vid)补齐播放地址/封面等。
    val firstVideo = Video(
        scm = null,
        coverUrl = item.cover,
        height = 1920,
        width = 1080,
        title = item.name,
        description = item.name,
        commentCount = 0,
        shareCount = 0,
        creator = Subscribers(userId = 0L, avatarUrl = "", nickname = item.artist),
        previewUrl = "",
        relateSong = null,
        vid = mvid.toString(),
        durationms = 0,
        playTime = 0,
        praisedCount = 0,
    )
    val json = Uri.encode(Gson().toJson(firstVideo))
    EchoNavController.instance.navigate("${RouterUrls.PLAY_VIDEO}/$json/0/0")
}
