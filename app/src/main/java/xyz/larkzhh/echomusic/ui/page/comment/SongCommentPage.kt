package xyz.larkzhh.echomusic.ui.page.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import xyz.larkzhh.echomusic.model.SongBean
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider

@Composable
fun SongCommentPage(songBean: SongBean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.background),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "${songBean.name} 的评论")
    }
}
