package xyz.larkzhh.echomusic.ui.page.discovery.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.model.DiscoverBannerItem
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.utils.cdp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CpnDiscoveryBanner(banners: List<DiscoverBannerItem>) {
    val displayBanners = banners.ifEmpty { listOf<DiscoverBannerItem?>(null) }
    val manualPauseMs = 5000L
    var lastManualSwipeAt by remember { mutableLongStateOf(0L) }
    var isAutoScrolling by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { displayBanners.size }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }
            .collectLatest { inProgress ->
                if (inProgress && !isAutoScrolling) {
                    lastManualSwipeAt = System.currentTimeMillis()
                }
            }
    }

    LaunchedEffect(displayBanners.size) {
        if (displayBanners.size <= 1) return@LaunchedEffect
        while (true) {
            val elapsed = System.currentTimeMillis() - lastManualSwipeAt
            if (elapsed in 1 until manualPauseMs) {
                delay(manualPauseMs - elapsed)
                continue
            }
            delay(3500)
            val nextPage = (pagerState.currentPage + 1) % displayBanners.size
            isAutoScrolling = true
            runCatching {
                pagerState.animateScrollToPage(nextPage)
            }
            isAutoScrolling = false
        }
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 24.cdp)
            .fillMaxWidth()
            .height(300.cdp)
            .clip(RoundedCornerShape(20.cdp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .matchParentSize()
                .clickable {
                    val url = displayBanners.getOrNull(pagerState.currentPage)?.url
                    openBannerUrl(url)
                }
        ) { page ->
            val banner = displayBanners.getOrNull(page)
            val coverUrl = banner?.bigImageUrl ?: banner?.imageUrl ?: banner?.pic
            CommonNetworkImage(
                url = coverUrl,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = R.drawable.ic_default_placeholder_video,
                error = R.drawable.ic_default_placeholder_video
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.cdp),
            horizontalArrangement = Arrangement.spacedBy(8.cdp)
        ) {
            repeat(displayBanners.size.coerceAtMost(8)) {
                Box(
                    modifier = Modifier
                        .size(8.cdp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (it == (pagerState.currentPage % displayBanners.size)) {
                                Color.White
                            } else {
                                Color.White.copy(alpha = 0.45f)
                            }
                        )
                )
            }
        }
    }
}

private fun openBannerUrl(rawUrl: String?) {
    if (rawUrl.isNullOrBlank()) return
    val targetUrl = normalizeBannerUrl(rawUrl)
    val encoded = Uri.encode(targetUrl)
    EchoNavController.instance.navigate("${RouterUrls.WEB}/$encoded")
}

private fun normalizeBannerUrl(rawUrl: String): String {
    if (rawUrl.startsWith("http://", ignoreCase = true) || rawUrl.startsWith("https://", ignoreCase = true)) {
        return rawUrl
    }
    if (!rawUrl.startsWith("orpheus://", ignoreCase = true)) {
        return rawUrl
    }
    val uri = Uri.parse(rawUrl)
    val host = uri.host.orEmpty()
    val id = uri.lastPathSegment.orEmpty()
    return when (host) {
        "playlist" -> "https://music.163.com/#/playlist?id=$id"
        "song" -> "https://music.163.com/#/song?id=$id"
        "album" -> "https://music.163.com/#/album?id=$id"
        else -> rawUrl
    }
}
