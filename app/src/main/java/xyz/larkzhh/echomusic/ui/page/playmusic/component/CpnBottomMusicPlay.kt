package xyz.larkzhh.echomusic.ui.page.playmusic.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import xyz.larkzhh.echomusic.core.MusicPlayController
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.compose.CircleProgress
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.compose.CommonLocalImage
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.ui.page.playmusic.showPlayListSheet
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.utils.csp

@Composable
fun BoxScope.CpnBottomPlayMusic() {
    if (MusicPlayController.originSongList.isNotEmpty()) {
        val curRouteName = EchoNavController.instance.currentBackStackEntryAsState().value?.destination?.route
        if (curRouteName == RouterUrls.HOME
            || curRouteName == RouterUrls.PROFILE
            || curRouteName?.split("/")?.getOrNull(0) == RouterUrls.PLAY_LIST
        ) {
            val paddingBottom = animateDpAsState(
                targetValue = if (curRouteName == RouterUrls.HOME) {
                    160.cdp
                } else {
                    0.dp
                }
            )
            AnimatedVisibility(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = paddingBottom.value),
                visibleState = remember { MutableTransitionState(false) }
                    .apply { targetState = MusicPlayController.showCpnBottomMusicPlay },
                enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(200)),
                exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(200))
            ) {
                BottomMusicPlayBar()
            }
        }
    }
}

@Composable
private fun BottomMusicPlayBar() {
    val diskRotateAngle by remember {
        mutableStateOf(Animatable(0f))
    }
    var lastDiskRotateAngleForSnap by remember { mutableStateOf(0f) }

    LaunchedEffect(MusicPlayController.isPlaying()) {
        if (MusicPlayController.isPlaying()) {
            diskRotateAngle.snapTo(lastDiskRotateAngleForSnap)
            diskRotateAngle.animateTo(
                targetValue = 360f + lastDiskRotateAngleForSnap,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            lastDiskRotateAngleForSnap = diskRotateAngle.value
            diskRotateAngle.stop()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.cdp)
            .clickable {
                MusicPlayController.showPlayMusicSheet = true
                MusicPlayController.showCpnBottomMusicPlay = true
            }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColorsProvider.current.bottomMusicPlayBarBackground)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 42.cdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(104.cdp)
                    .offset(y = (-18).cdp),
                contentAlignment = Alignment.Center
            ) {
                CommonLocalImage(
                    R.drawable.ic_disc,
                    modifier = Modifier.fillMaxSize()
                )
                CommonNetworkImage(
                    MusicPlayController.originSongList[MusicPlayController.curOriginIndex].al.picUrl,
                    placeholder = R.drawable.ic_default_disk_cover,
                    error = R.drawable.ic_default_disk_cover,
                    modifier = Modifier.size(70.cdp)
                        .clip(CircleShape)
                        .rotate(diskRotateAngle.value)
                )
            }

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = AppColorsProvider.current.firstText, fontSize = 30.csp)) {
                        append(MusicPlayController.originSongList[MusicPlayController.curOriginIndex].name)
                    }
                    withStyle(style = SpanStyle(color = AppColorsProvider.current.secondText, fontSize = 24.csp)) {
                        append(" - ${MusicPlayController.originSongList[MusicPlayController.curOriginIndex].ar[0].name}")
                    }
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 22.cdp, end = 32.cdp, bottom = 8.cdp)
            )

            Box(
                Modifier
                    .padding(end = 16.cdp)
                    .size(75.cdp)
                    .clip(CircleShape)
                    .clickable {
                        if (MusicPlayController.isPlaying()) {
                            MusicPlayController.pause()
                        } else {
                            MusicPlayController.resume()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                CommonIcon(
                    if (!MusicPlayController.isPlaying()) R.drawable.ic_play_without_circle else R.drawable.ic_pause_without_circle,
                    tint = Color.Gray,
                    modifier = Modifier.size(30.cdp)
                )
                CircleProgress(modifier = Modifier.size(58.cdp), MusicPlayController.progress)
            }

            CommonIcon(
                R.drawable.ic_play_list,
                tint = Color.Gray,
                modifier = Modifier.size(75.cdp)
                    .clip(CircleShape)
                    .clickable {
                        showPlayListSheet = true
                    }
                    .padding(12.cdp)
            )
        }
    }
}


