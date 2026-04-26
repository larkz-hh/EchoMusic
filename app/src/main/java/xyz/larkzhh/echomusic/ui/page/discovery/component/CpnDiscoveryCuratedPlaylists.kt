package xyz.larkzhh.echomusic.ui.page.discovery.component

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.google.gson.Gson
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.model.PlaylistBean
import xyz.larkzhh.echomusic.model.Subscribers
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp

fun defaultCuratedPlaylists(): List<DiscoveryCuratedPlaylistItem> = DEFAULT_CURATED_PLAYLISTS

@Composable
fun CpnDiscoveryCuratedPlaylists(
    title: String = "甄选歌单",
    playlists: List<DiscoveryCuratedPlaylistItem> = DEFAULT_CURATED_PLAYLISTS,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 24.cdp),
            color = AppColorsProvider.current.firstText,
            fontSize = 36.csp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.cdp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.cdp)
        ) {
            item { Spacer(modifier = Modifier.size(10.cdp)) }
            items(playlists) { item ->
                Column(
                    modifier = Modifier
                        .width(180.cdp)
                        .clickable { openPlaylist(item) }
                ) {
                    CommonNetworkImage(
                        url = item.cover,
                        modifier = Modifier
                            .size(180.cdp)
                            .clip(RoundedCornerShape(14.cdp)),
                        placeholder = R.drawable.ic_default_placeholder_video,
                        error = R.drawable.ic_default_placeholder_video
                    )
                    Spacer(modifier = Modifier.height(10.cdp))
                    Text(
                        text = item.title,
                        color = AppColorsProvider.current.firstText,
                        fontSize = 20.csp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            item { Spacer(modifier = Modifier.size(10.cdp)) }
        }
    }
}

data class DiscoveryCuratedPlaylistItem(
    val playlistId: Long?,
    val cover: String,
    val title: String,
    val playCount: Long?,
    val description: String?,
)

private val DEFAULT_CURATED_PLAYLISTS = listOf(
    DiscoveryCuratedPlaylistItem(
        playlistId = 122520070L,
        cover = "https://p1.music.126.net/AK63FHhhVnvNfH5Mv0qWrA==/109951165445526996.jpg",
        title = "怀念张国荣：宠爱未完，想念有声",
        playCount = 3262L,
        description = null,
    ),
    DiscoveryCuratedPlaylistItem(
        playlistId = 7179928202L,
        cover = "https://p1.music.126.net/IvqnoB9_EM9jrxzfUc90oA==/109951163766031314.jpg",
        title = "华语柔情｜白茶清欢无别事",
        playCount = 33L,
        description = null,
    ),
    DiscoveryCuratedPlaylistItem(
        playlistId = 7107570550L,
        cover = "https://p1.music.126.net/FFUdbekZJf8Sz9yqEy3bcw==/109951165994448179.jpg",
        title = "华语忧伤｜情深缘浅徒留心酸",
        playCount = 292L,
        description = null,
    )
)

private fun openPlaylist(item: DiscoveryCuratedPlaylistItem) {
    val playlistBean = PlaylistBean(
        tracks = null,
        trackIds = null,
        creator = Subscribers(
            userId = item.playlistId ?: 0L,
            avatarUrl = "",
            nickname = item.title,
        ),
        name = item.title,
        coverImgUrl = item.cover,
        trackCount = 0,
        id = item.playlistId ?: 0L,
        playCount = item.playCount ?: 0L,
        description = item.description,
        shareCount = 0,
        commentCount = 0,
    )
    val json = Uri.encode(Gson().toJson(playlistBean))
    EchoNavController.instance.navigate("${RouterUrls.PLAY_LIST}/$json")
}
