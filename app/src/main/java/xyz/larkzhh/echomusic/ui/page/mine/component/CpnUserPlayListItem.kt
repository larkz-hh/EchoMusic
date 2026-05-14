package xyz.larkzhh.echomusic.ui.page.mine.component

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.model.PlaylistBean
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import xyz.larkzhh.echomusic.R

@Composable
fun CpnUserPlayListItem(platListBean: PlaylistBean?, horizontalPadding: Dp = 32.cdp) {
    Row(
        Modifier
            .padding(horizontal = horizontalPadding)
            .background(AppColorsProvider.current.card)
            .fillMaxWidth()
            .height(126.cdp)
            .clickable {
                val json = Uri.encode(Gson().toJson(platListBean))
                EchoNavController.instance.navigate("${RouterUrls.PLAY_LIST}/$json")
            }
            .padding(start = 32.cdp, end = 32.cdp, top = 8.cdp, bottom = 8.cdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        platListBean?.let {
            CommonNetworkImage(
                url = it.coverImgUrl,
                modifier = Modifier
                    .padding(end = 20.cdp)
                    .size(100.cdp)
                    .clip(RoundedCornerShape(10.cdp)),
            )

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = it.name,
                    fontSize = 30.csp,
                    color = AppColorsProvider.current.firstText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "共${it.trackCount}首",
                    fontSize = 24.csp,
                    color = AppColorsProvider.current.secondText,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_sheet_menu),
                contentDescription = "",
                modifier = Modifier.height(30.cdp)
            )
        }
    }

}









