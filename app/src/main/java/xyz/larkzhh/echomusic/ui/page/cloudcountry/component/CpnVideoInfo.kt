package xyz.larkzhh.echomusic.ui.page.cloudcountry.component

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import xyz.larkzhh.echomusic.model.Video
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.hilt.entrypoint.EntryPointFinder
import xyz.larkzhh.echomusic.ui.page.comment.showVideoCommentSheet
import xyz.larkzhh.echomusic.utils.StringUtil
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.onClick
import xyz.larkzhh.echomusic.utils.showToast
import xyz.larkzhh.echomusic.viewmodel.VideoPlayViewModel
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.csp

@Composable
fun BoxScope.CpnVideoInfo(video: Video) {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    Column(
        modifier = Modifier
            .padding(start = 32.cdp, end = 16.cdp)
            .fillMaxWidth()
            .graphicsLayer { alpha = viewModel.videoInfoAlpha }
            .align(Alignment.BottomCenter)
    ) {
        VideoInfoComponent(video)
        BottomComponent()
    }
}

@Composable
private fun VideoInfoComponent(video: Video) {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val api = remember { EntryPointFinder.getEchoApi() }
    val context = LocalContext.current
    val vid = video.vid ?: ""
    var liked by remember(vid) { mutableStateOf(false) }
    var likedCount by remember(vid) { mutableIntStateOf(video.praisedCount) }
    var collected by remember(vid) { mutableStateOf(false) }
    if (viewModel.showVideoInfo) {
        Row(
            //modifier = Modifier.padding(bottom = 40.cdp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CommonNetworkImage(
                        url = video.creator?.avatarUrl,
                        placeholder = R.drawable.ic_default_avator,
                        error = R.drawable.ic_default_avator,
                        modifier = Modifier.size(55.cdp)
                            .clip(
                                RoundedCornerShape(50)
                            )
                    )
                    Text(
                        text = video.creator?.nickname ?: "未知",
                        fontSize = 32.csp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(start = 16.cdp)
                            .weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = video.title ?: "",
                    maxLines = 4,
                    modifier = Modifier.padding(top = 24.cdp),
                    fontSize = 28.csp,
                    color = Color.White,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.cdp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val relateSong = video.relateSong?.getOrNull(0)
                    val songInfo = if (relateSong != null) {
                        "${relateSong.name}-${relateSong.ar.getOrNull(0)?.name ?: "未知"}"
                    } else {
                        "未知-未知"
                    }
                    CommonIcon(
                        resId = R.drawable.ic_music_logo,
                        modifier = Modifier.size(30.cdp),
                        tint = Color.White
                    )
                    Text(
                        text = songInfo,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 24.cdp),
                        fontSize = 28.csp,
                        color = Color.White,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                VideoActionButton(
                    R.drawable.ic_video_fabulous,
                    StringUtil.friendlyNumber(likedCount),
                    tint = if (liked) Color(0xFFFF4D4F) else Color.White
                ) {
                    if (vid.isBlank()) {
                        return@VideoActionButton
                    }
                    val target = !liked
                    liked = target
                    likedCount = (likedCount + if (target) 1 else -1).coerceAtLeast(0)
                    scope.launch {
                        val result = runCatching {
                            api.likeResource(id = vid, type = 5, t = if (target) 1 else 0)
                        }
                        if (result.getOrNull()?.resultOk() == true) {
                            showToast(if (target) "已点赞" else "已取消点赞")
                        } else {
                            liked = !target
                            likedCount = (likedCount + if (target) -1 else 1).coerceAtLeast(0)
                            showToast(
                                result.getOrNull()?.message?.takeIf { it.isNotBlank() }
                                    ?: "操作失败，请检查登录状态"
                            )
                        }
                    }
                }
                VideoActionButton(
                    R.drawable.ic_video_comment,
                    StringUtil.friendlyNumber(video.commentCount)
                ) {
                    showVideoCommentSheet = true
                }
                VideoActionButton(
                    R.drawable.ic_video_share,
                    StringUtil.friendlyNumber(video.shareCount)
                ) {
                    if (vid.isNotBlank()) {
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "https://music.163.com/video/$vid")
                        }
                        context.startActivity(Intent.createChooser(share, "分享视频"))
                    }
                }
                VideoActionButton(
                    R.drawable.ic_video_collect,
                    if (collected) "已收藏" else "收藏",
                    paddingBottom = 10.cdp,
                    tint = if (collected) AppColorsProvider.current.primary else Color.White
                ) {
                    if (vid.isBlank()) {
                        return@VideoActionButton
                    }
                    val target = !collected
                    collected = target
                    scope.launch {
                        val result = runCatching {
                            api.collectVideo(id = vid, t = if (target) 1 else 0)
                        }
                        if (result.getOrNull()?.resultOk() == true) {
                            showToast(if (target) "已收藏" else "已取消收藏")
                        } else {
                            collected = !target
                            showToast(
                                result.getOrNull()?.message?.takeIf { it.isNotBlank() }
                                    ?: "操作失败，请检查登录状态"
                            )
                        }
                    }
                }

                CpnVideoSongDisk(video)
            }
        }
    }
}

@Composable
private fun VideoActionButton(
    iconResId: Int,
    text: String,
    paddingBottom: Dp = 54.cdp,
    tint: Color = Color.White,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .padding(bottom = paddingBottom, start = 60.cdp)
            .wrapContentSize()
            .onClick(enableRipple = false) {
                onClick?.invoke()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CommonIcon(
            resId = iconResId,
            tint = tint,
            modifier = Modifier
                .padding(bottom = 12.cdp)
                .size(48.cdp)
        )

        Text(
            text = text,
            color = Color.White,
            fontSize = 28.csp
        )
    }
}

@Composable
private fun BottomComponent() {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(cpnBottomSendCommentHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "千言万语,汇成评论一句话", fontSize = 28.csp, color = AppColorsProvider.current.thirdText,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier.size(54.cdp)
                .onClick {
                    viewModel.showVideoInfo = !viewModel.showVideoInfo
                }
                .clip(RoundedCornerShape(50))
                .background(Color.Gray.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CommonIcon(
                resId = if (viewModel.showVideoInfo) R.drawable.ic_video_info_hide else R.drawable.ic_video_info_show,
                modifier = Modifier.size(32.cdp),
                tint = Color.White
            )
        }
    }
}
