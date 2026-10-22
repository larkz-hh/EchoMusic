package xyz.larkzhh.echomusic.ui.page.playmusic.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import xyz.larkzhh.echomusic.core.audio.EqController
import xyz.larkzhh.echomusic.core.audio.EqPreset
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppColorsProvider.current.card
    ) {
        Column(modifier = Modifier.padding(horizontal = 32.cdp, vertical = 16.cdp)) {
            Text(
                text = "音效",
                fontSize = 30.csp,
                fontWeight = FontWeight.Bold,
                color = AppColorsProvider.current.firstText
            )
            EqPreset.values().forEach { preset ->
                val selected = EqController.preset == preset
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            EqController.apply(preset)
                            onDismiss()
                        }
                        .padding(vertical = 18.cdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = preset.title,
                        fontSize = 28.csp,
                        color = if (selected) AppColorsProvider.current.primary
                        else AppColorsProvider.current.firstText,
                        modifier = Modifier.weight(1f)
                    )
                    if (selected) {
                        Text(
                            text = "使用中",
                            fontSize = 22.csp,
                            color = AppColorsProvider.current.primary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.cdp))
        }
    }
}
