package xyz.larkzhh.echomusic.ui.page.cloudcountry.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.larkzhh.echomusic.model.Video
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.compose.CommonLocalImage
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.toPx
import xyz.larkzhh.echomusic.viewmodel.VideoPlayViewModel
import xyz.larkzhh.echomusic.R


private val diskAnimState by mutableStateOf(Animatable(0f))
private var lastDiskAnimStateForSnap by mutableFloatStateOf(0f)
private val maxAnimOffsetXRange = 20.cdp.toPx
private val maxAnimOffsetYRange = 120.cdp.toPx
private var logo1OffsetX = 0
private var logo1OffsetY = 0
private var logo2OffsetX = 0
private var logo2OffsetY = 0
private var logo1Alpha = 0f
private var logo2Alpha = 0f
private var logo1Scale = 0f
private var logo2Scale = 0f

@Composable
fun CpnVideoSongDisk(video: Video) {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    val relateSong = video.relateSong?.getOrNull(0)

    val curItemVideoUrl = video.urls?.getOrNull(0)?.url ?: ""
    if (relateSong != null && curItemVideoUrl == viewModel.curVideoUrl) {
        LaunchedEffect(viewModel.videoPlaying) {
            if (viewModel.videoPlaying) {
                diskAnimState.snapTo(lastDiskAnimStateForSnap)
                diskAnimState.animateTo(
                    targetValue = 360f + lastDiskAnimStateForSnap,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 10000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            } else {
                lastDiskAnimStateForSnap = diskAnimState.value % 360
                diskAnimState.stop()
            }
        }
    }

    val curDiskRotateValue = diskAnimState.value % 360

    Box(
        modifier = Modifier
            .padding(start = 20.cdp, bottom = 32.cdp)
            .size(130.cdp)
    ) {
        Box(
            modifier = Modifier
                .size(90.cdp)
                .align(Alignment.BottomEnd),
            contentAlignment = Alignment.Center
        ) {
            CommonLocalImage(
                R.drawable.ic_disc,
                modifier = Modifier.fillMaxSize()
            )
            CommonNetworkImage(
                relateSong?.al?.picUrl,
                placeholder = R.drawable.ic_default_disk_cover,
                error = R.drawable.ic_default_disk_cover,
                modifier = Modifier.size(56.cdp)
                    .clip(CircleShape)
                    .graphicsLayer {
                        rotationZ = curDiskRotateValue
                    }
            )
        }


        if (relateSong != null && curItemVideoUrl == viewModel.curVideoUrl) {

            val curLogo1Value = curDiskRotateValue % 180 / 180f

            if (curLogo1Value <= 0.5f) {
                logo1OffsetX = (maxAnimOffsetXRange - curLogo1Value * 2 * maxAnimOffsetXRange).toInt()
                logo1Alpha = curLogo1Value * 2
                logo1Scale = logo1Alpha
            } else {
                logo1OffsetX = ((curLogo1Value - 0.5f) * 2 * maxAnimOffsetXRange * 1.4f).toInt()
                logo1Alpha = 1 - (curLogo1Value - 0.5f) * 2
            }

            val curLogo2Value = (curDiskRotateValue + 90) % 180 / 180f

            if (curLogo2Value <= 0.5f) {
                logo2OffsetX = (maxAnimOffsetXRange - curLogo2Value * 2 * maxAnimOffsetXRange).toInt()
                logo2Alpha = curLogo2Value * 2
                logo2Scale = logo2Alpha
            } else {
                logo2OffsetX = ((curLogo2Value - 0.5) * 2 * maxAnimOffsetXRange * 1.4f).toInt()
                logo2Alpha = 1 - (curLogo2Value - 0.5f) * 2
            }

            logo1OffsetY = -(maxAnimOffsetYRange * curLogo1Value).toInt()
            logo2OffsetY = -(maxAnimOffsetYRange * curLogo2Value).toInt()

            CommonIcon(
                resId = R.drawable.ic_music_logo,
                modifier = Modifier.size(28.cdp)
                    .align(Alignment.BottomStart)
                    .offset { IntOffset(logo1OffsetX, logo1OffsetY) }
                    .graphicsLayer {
                        alpha = logo1Alpha
                        rotationZ = curDiskRotateValue
                        scaleX = logo1Scale
                        scaleY = logo1Scale
                    },
                tint = Color.White
            )

            CommonIcon(
                resId = R.drawable.ic_music_logo2,
                modifier = Modifier.size(28.cdp)
                    .align(Alignment.BottomStart)
                    .offset { IntOffset(logo2OffsetX, logo2OffsetY) }
                    .graphicsLayer {
                        alpha = logo2Alpha
                        rotationZ = curDiskRotateValue
                        scaleX = logo2Scale
                        scaleY = logo2Scale
                    },
                tint = Color.White
            )
        }
    }
}

