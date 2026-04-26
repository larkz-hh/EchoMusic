package xyz.larkzhh.echomusic.ui.page.discovery.component

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.google.gson.Gson
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.model.PlaylistBean
import xyz.larkzhh.echomusic.model.Subscribers
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider

data class DiscoveryRankingItem(
    val title: String,
    val playlistId: Long?,
    val playlistName: String?,
    val coverImgUrl: String?,
    val description: String?,
    val playCount: Long?,
    val gradient: List<Color>,
    val topSongs: String,
)

@Composable
fun CpnDiscoveryRanking(items: List<DiscoveryRankingItem>) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.cdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "排行榜",
                color = AppColorsProvider.current.firstText,
                fontSize = 38.csp,
            )
            Spacer(modifier = Modifier.size(10.cdp))
            Text(
                text = "›",
                color = AppColorsProvider.current.secondText,
                fontSize = 28.csp,
            )
        }

        Spacer(modifier = Modifier.height(12.cdp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.cdp),
            horizontalArrangement = Arrangement.spacedBy(16.cdp)
        ) {
            items(items = items) { item ->
                Column(modifier = Modifier.width(200.cdp)) {
                    Box(
                        modifier = Modifier
                            .size(width = 200.cdp, height = 200.cdp)
                            .clip(RoundedCornerShape(24.cdp))
                            .background(Brush.linearGradient(colors = item.gradient))
                            .clickable { openPlaylist(item) }
                    ) {
                        Text(
                            text = item.title,
                            color = Color.White,
                            fontSize = 26.csp,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                        CommonIcon(
                            resId = R.drawable.ic_video_play,
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 16.cdp, bottom = 16.cdp)
                                .size(48.cdp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.cdp))
                }
            }
        }
    }
}

private fun openPlaylist(item: DiscoveryRankingItem) {
    val playlistId = item.playlistId ?: 0L
    val playlistBean = PlaylistBean(
        tracks = null,
        trackIds = null,
        creator = Subscribers(
            userId = playlistId,
            avatarUrl = "",
            nickname = item.playlistName.orEmpty(),
        ),
        name = item.playlistName.orEmpty(),
        coverImgUrl = item.coverImgUrl.orEmpty(),
        trackCount = 0,
        id = playlistId,
        playCount = item.playCount ?: 0L,
        description = item.description,
        shareCount = 0,
        commentCount = 0,
    )
    val json = Uri.encode(Gson().toJson(playlistBean))
    EchoNavController.instance.navigate("${RouterUrls.PLAY_LIST}/$json")
}

