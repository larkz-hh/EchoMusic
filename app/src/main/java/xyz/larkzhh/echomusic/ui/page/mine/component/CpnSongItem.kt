package xyz.larkzhh.echomusic.ui.page.mine.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import xyz.larkzhh.echomusic.core.MusicPlayController
import xyz.larkzhh.echomusic.model.SongBean
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import xyz.larkzhh.echomusic.ui.page.playmusic.component.CpnPlayingMark
import xyz.larkzhh.echomusic.utils.onClick
import xyz.larkzhh.echomusic.R

@Composable
fun CpnSongItem(index: Int, songBean: SongBean, onClick: (index: Int) -> Unit) {
    Row(
        Modifier.onClick {
            onClick.invoke(index)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (MusicPlayController.isPlaying(songBean)) {
            CpnPlayingMark(playing = MusicPlayController.isPlaying(), modifier = Modifier.width(120.cdp))
        } else {
            Text(
                text = (index + 1).toString(),
                fontSize = 30.csp,
                color = AppColorsProvider.current.secondText,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(120.cdp)
            )
        }

        Column(
            modifier = Modifier
                .padding(vertical = 26.cdp)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = songBean.name,
                fontSize = 32.csp,
                color = AppColorsProvider.current.firstText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = songBean.ar.getOrNull(0)?.name ?: "未知",
                fontSize = 24.csp,
                color = AppColorsProvider.current.secondText,
                modifier = Modifier.padding(top = 10.cdp)
            )
        }

        CommonIcon(
            resId = R.drawable.ic_sheet_menu,
            modifier = Modifier.height(32.cdp)
                .padding(horizontal = 32.cdp)
        )
    }
}