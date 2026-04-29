package xyz.larkzhh.echomusic.ui.page.singer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import kotlinx.coroutines.launch

@Composable
fun CpnSingerSideBar(
    keys: List<String>,
    listState: LazyListState,
    keyToIndex: Map<String, Int>,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.cdp))
            .background(Color.Black.copy(alpha = 0.08f))
            .padding(vertical = 6.cdp, horizontal = 6.cdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        keys.forEach { key ->
            Text(
                text = key,
                color = AppColorsProvider.current.firstText,
                fontSize = 14.csp,
                modifier = Modifier
                    .padding(vertical = 1.cdp)
                    .alpha(0.9f)
                    .clip(RoundedCornerShape(4.cdp))
                    .padding(horizontal = 2.cdp)
                    .clickable {
                        keyToIndex[key]?.let { target ->
                            scope.launch {
                                listState.animateScrollToItem(target)
                            }
                        }
                    },
            )
        }
    }
}
