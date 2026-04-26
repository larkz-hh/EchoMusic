package xyz.larkzhh.echomusic.ui.page.discovery.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp

@Composable
fun CpnDiscoveryEntryGrid() {
    Column(
        modifier = Modifier.padding(horizontal = 24.cdp),
        verticalArrangement = Arrangement.spacedBy(12.cdp)
    ) {
        DiscoveryEntryRow(ENTRY_LIST.take(4))
        DiscoveryEntryRow(ENTRY_LIST.drop(4))
    }
}

@Composable
private fun DiscoveryEntryRow(items: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.cdp)
    ) {
        items.forEach { title ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(60.cdp)
                    .clip(RoundedCornerShape(12.cdp))
                    .background(AppColorsProvider.current.card)
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 6.cdp),
                    color = AppColorsProvider.current.firstText,
                    fontSize = 22.csp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

private val ENTRY_LIST = listOf(
    "排行榜", "歌单", "原创", "VIP",
    "经典", "欧美", "粤语", "驾车"
)
