package xyz.larkzhh.echomusic.ui.page.playmusic.component

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.gson.Gson
import xyz.larkzhh.echomusic.core.AppGlobalData
import xyz.larkzhh.echomusic.core.LikeStore
import xyz.larkzhh.echomusic.core.MusicPlayController
import xyz.larkzhh.echomusic.hilt.entrypoint.EntryPointFinder
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.core.player.PlayMode
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.compose.SeekBar
import xyz.larkzhh.echomusic.ui.page.playmusic.showPlayListSheet
import xyz.larkzhh.echomusic.utils.StringUtil
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.onClick
import xyz.larkzhh.echomusic.utils.showToast
import xyz.larkzhh.echomusic.viewmodel.PlayMusicViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.utils.csp

@Composable
fun CpnPlayMusicActionLayout() {
    MiddleActionLayout()
    ProgressLayout()
    BottomActionLayout()
}

@Composable
private fun MiddleActionLayout() {
    val viewModel: PlayMusicViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val api = remember { EntryPointFinder.getEchoApi() }
    val currentSong = MusicPlayController.realSongList.getOrNull(MusicPlayController.curRealIndex)
    val isLocal = currentSong?.localUri != null

    LaunchedEffect(MusicPlayController.curRealIndex) {
        // 本地歌曲没有网易云 id，跳过评论和歌词
        if (!isLocal) {
            viewModel.getSongComment(MusicPlayController.realSongList[MusicPlayController.curRealIndex])
            viewModel.getLyric(MusicPlayController.realSongList[MusicPlayController.curRealIndex])
            LikeStore.load(api, AppGlobalData.currentUid)
        }
    }

    Row(
        modifier = Modifier
            .padding(start = 60.cdp, end = 60.cdp, bottom = 32.cdp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val songId = currentSong?.id ?: 0L
        MiddleActionIcon(
            resId = R.drawable.ic_like_no,
            tint = if (LikeStore.isLiked(songId)) Color(0xFFFF4D4F) else Color.White,
            modifier = Modifier.padding(end = 60.cdp)
        ) {
            if (isLocal) {
                showToast("本地音乐不支持点赞")
                return@MiddleActionIcon
            }
            if (songId == 0L) {
                return@MiddleActionIcon
            }
            val target = !LikeStore.isLiked(songId)
            LikeStore.setLiked(songId, target)
            scope.launch {
                val result = runCatching { api.like(songId, target) }
                if (result.isSuccess) {
                    showToast(if (target) "已喜欢" else "已取消喜欢")
                } else {
                    LikeStore.setLiked(songId, !target)
                    showToast(errorMessage(result.exceptionOrNull()) ?: "操作失败，请检查登录状态")
                }
            }
        }
        MiddleActionIcon(R.drawable.ic_download, modifier = Modifier.padding(end = 60.cdp))
        MiddleActionIcon(R.drawable.ic_action_sing, modifier = Modifier.padding(end = 60.cdp))
        Box(modifier = Modifier.width(138.cdp)) {
            MiddleActionIcon(R.drawable.ic_comment_count) {
                if (isLocal) {
                    showToast("本地音乐没有评论")
                    return@MiddleActionIcon
                }
                val json =
                    Uri.encode(Gson().toJson(MusicPlayController.realSongList[MusicPlayController.curRealIndex]))
                EchoNavController.instance.navigate("${RouterUrls.SONG_COMMENT}/$json")
            }
            if (!isLocal) {
                viewModel.songCommentResult?.let {
                    val commentText = StringUtil.friendlyNumber(it.total)
                    Text(
                        text = commentText,
                        color = Color.White,
                        fontSize = 18.csp,
                        modifier = Modifier
                            .padding(top = 6.cdp, start = 48.cdp)
                            .align(Alignment.TopStart)
                    )
                }
            }
        }
        MiddleActionIcon(R.drawable.ic_song_more)
    }
}

@Composable
private fun MiddleActionIcon(
    resId: Int,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    clickable: () -> Unit = {}
) {
    CommonIcon(
        resId,
        tint = tint,
        modifier = modifier
            .size(78.cdp)
            .clip(CircleShape)
            .clickable {
                clickable.invoke()
            }
            .padding(16.cdp)
    )
}

private fun errorMessage(error: Throwable?): String? {
    val body = (error as? HttpException)?.response()?.errorBody()?.string() ?: return null
    return runCatching { JSONObject(body).optString("message") }.getOrNull()?.takeIf { it.isNotBlank() }
}

@Composable
private fun ProgressLayout() {
    Row(
        modifier = Modifier
            .padding(start = 44.cdp, end = 44.cdp, bottom = 32.cdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = MusicPlayController.curPositionStr,
            fontSize = 26.csp,
            color = Color.White,
            modifier = Modifier.width(110.cdp)
        )
        SeekBar(
            progress = MusicPlayController.progress,
            seeking = {
                MusicPlayController.seeking(it)
            },
            seekTo = {
                MusicPlayController.seekTo(it)
            },
            modifier = Modifier
                .weight(1f)
                .height(80.cdp)
        )
        Text(
            text = MusicPlayController.totalDuringStr,
            fontSize = 26.csp,
            color = Color.White,
            modifier = Modifier.width(110.cdp),
            textAlign = TextAlign.End
        )
    }

}


@Composable
private fun BottomActionLayout() {
    val viewModel: PlayMusicViewModel = hiltViewModel()
    val coroutineScopeScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .padding(start = 20.cdp, end = 20.cdp, bottom = 60.cdp)
            .fillMaxWidth()
            .height(120.cdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val playModeResId = when (MusicPlayController.playMode) {
            PlayMode.RANDOM -> R.drawable.ic_play_mode_random
            PlayMode.SINGLE -> R.drawable.ic_play_mode_single
            PlayMode.LOOP -> R.drawable.ic_play_mode_loop
        }
        BottomActionIcon(playModeResId) {
            when (MusicPlayController.playMode) {
                PlayMode.RANDOM -> MusicPlayController.changePlayMode(PlayMode.SINGLE)
                PlayMode.SINGLE -> MusicPlayController.changePlayMode(PlayMode.LOOP)
                PlayMode.LOOP -> MusicPlayController.changePlayMode(PlayMode.RANDOM)
            }
        }
        // 播放上一曲
        BottomActionIcon(R.drawable.ic_action_pre) {
            //sheetNeedleUp = true
            val newIndex = MusicPlayController.getPreRealIndex()
            Log.e("cloudMusic", "播放上一曲 newIndex=${newIndex}")
            coroutineScopeScope.launch {
                viewModel.sheetDiskRotate.stop()
                viewModel.lastSheetDiskRotateAngleForSnap = 0f
                // pagerState.animateScrollToPage(newIndex, animationSpec = tween(400))
                MusicPlayController.playByRealIndex(newIndex)
            }
        }
        // 播放or暂停
        BottomActionIcon(
            if (MusicPlayController.isPlaying()) R.drawable.ic_action_pause else R.drawable.ic_action_play,
            size = 116
        ) {
            if (MusicPlayController.isPlaying()) {
                MusicPlayController.pause()
                coroutineScopeScope.launch {
                    viewModel.sheetNeedleUp = true
                    viewModel.lastSheetDiskRotateAngleForSnap = viewModel.sheetDiskRotate.value
                    viewModel.sheetDiskRotate.stop()
                }
            } else {
                MusicPlayController.resume()
            }
        }
        // 播放下一曲
        BottomActionIcon(R.drawable.ic_action_next) {
            val newIndex = MusicPlayController.getNextRealIndex()
            Log.e("cloudMusic", "播放下一曲 newIndex=${newIndex}")
            //sheetNeedleUp = true
            coroutineScopeScope.launch {
                viewModel.sheetDiskRotate.stop()
                viewModel.lastSheetDiskRotateAngleForSnap = 0f
                //pagerState.animateScrollToPage(newIndex, animationSpec = tween(400))
                MusicPlayController.playByRealIndex(newIndex)
            }
        }
        BottomActionIcon(R.drawable.ic_play_list) {
            showPlayListSheet = true
        }
    }
}


@Composable
private fun BottomActionIcon(
    resId: Int,
    size: Int = 84,
    enable: Boolean = true,
    onClick: () -> Unit = {}
) {
    CommonIcon(
        resId,
        tint = if (enable) Color.White else Color(0xFFBBBBBB),
        modifier = Modifier.size(size.cdp)
            .clip(CircleShape)
            .onClick(enableRipple = enable) {
                if (enable) {
                    onClick()
                }
            }
            .padding(16.cdp)
    )
}

