package xyz.larkzhh.echomusic.ui.page.cloudcountry

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.google.gson.Gson
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.larkzhh.echomusic.core.viewstate.ViewStateStaggeredGridPagingComponent
import xyz.larkzhh.echomusic.model.MvAllItem
import xyz.larkzhh.echomusic.model.Subscribers
import xyz.larkzhh.echomusic.model.Video
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterKV
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.StringUtil
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.onClick
import xyz.larkzhh.echomusic.viewmodel.CloudCountryViewModel
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.utils.csp


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoStaggeredGridPage(id: Int, position: Int) {
    val viewModel: CloudCountryViewModel = hiltViewModel()
    val loadPaging = remember(id) { { viewModel.buildVideoGroupPager(position) } }

    Box(modifier = Modifier.fillMaxSize()) {
        ViewStateStaggeredGridPagingComponent(
            modifier = Modifier
                .padding(horizontal = 24.cdp)
                .fillMaxSize(),
            key = "VideoStaggeredGridPage-${position}",
            loadDataBlock = loadPaging,
            columns = 2
        ) { loadedItems, _ ->
            items(loadedItems.size) { outerIndex ->
                loadedItems.getOrNull(outerIndex)?.let {
                    VideoItem(item = it, videoGroupId = position, videoOffsetIndex = outerIndex)
                }
            }
        }
    }
}

@Composable
private fun VideoItem(item: MvAllItem, videoGroupId: Int, videoOffsetIndex: Int) {
    Column(
        modifier = Modifier.padding(15.cdp)
            .clip(RoundedCornerShape(24.cdp))
            .background(AppColorsProvider.current.card)
            .onClick {
                val video = mapMvItemToVideo(item)
                val json = Uri.encode(Gson().toJson(video))
                EchoNavController.instance.navigate(
                    "${RouterUrls.PLAY_VIDEO}/$json/$videoGroupId/$videoOffsetIndex"
                )
            }
    ) {
        CommonNetworkImage(
            url = item.cover,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.cdp),
            placeholder = R.drawable.ic_default_placeholder_video,
            error = R.drawable.ic_default_placeholder_video
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 20.cdp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = item.name.orEmpty(),
                color = AppColorsProvider.current.firstText,
                fontSize = 26.csp,
                maxLines = 2,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.cdp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CommonNetworkImage(
                    url = null,
                    placeholder = R.drawable.ic_default_avator,
                    error = R.drawable.ic_default_avator,
                    modifier = Modifier.size(36.cdp)
                        .clip(
                            RoundedCornerShape(50)
                        )
                )
                Text(
                    text = item.artistName ?: "未知",
                    fontSize = 20.csp,
                    color = AppColorsProvider.current.secondText,
                    modifier = Modifier
                        .padding(start = 10.cdp)
                        .weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CommonIcon(
                        resId = R.drawable.ic_fabulous, modifier = Modifier.size(28.cdp),
                        tint = AppColorsProvider.current.secondIcon
                    )

                    Text(
                        text = StringUtil.friendlyNumber(item.playCount ?: 0),
                        fontSize = 24.csp,
                        color = AppColorsProvider.current.secondText,
                        modifier = Modifier
                            .padding(start = 10.cdp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun mapMvItemToVideo(item: MvAllItem): Video {
    return Video(
        scm = null,
        coverUrl = item.cover,
        height = 1920,
        width = 1080,
        title = item.name.orEmpty(),
        description = item.name.orEmpty(),
        commentCount = 0,
        shareCount = 0,
        creator = Subscribers(
            userId = 0L,
            avatarUrl = "",
            nickname = item.artistName ?: "未知"
        ),
        previewUrl = "",
        relateSong = null,
        vid = item.id?.toString(),
        durationms = item.duration?.toInt() ?: 0,
        playTime = item.playCount?.toInt() ?: 0,
        praisedCount = 0
    )
}