package xyz.larkzhh.echomusic.ui.page.mine.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp


@Composable
fun CpnPlayListPlaceHolder(tip: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 32.cdp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(bottomStart = 24.cdp, bottomEnd = 24.cdp))
            .background(AppColorsProvider.current.card)
            .padding(vertical = 80.cdp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = tip,
            color = AppColorsProvider.current.secondText,
            fontSize = 28.csp,
            modifier = Modifier.padding(bottom = 48.cdp)
        )

        Button(
            onClick = {},
            modifier = Modifier
                .padding(start = 160.cdp, end = 160.cdp, bottom = 40.cdp)
                .fillMaxWidth()
                .height(70.cdp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColorsProvider.current.primary
            ),
            content = {
                Text(text = "去添加", fontSize = 30.csp, color = Color.White)
            }
        )
    }
}