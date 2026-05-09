package xyz.larkzhh.echomusic.ui.page.playmusic

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import xyz.larkzhh.echomusic.ui.page.playmusic.component.CpnCurrentPlayList
import kotlinx.coroutines.launch


var showPlayListSheet by mutableStateOf(false)

@Composable
fun PlayListSheet() {
    if (showPlayListSheet) {
        PlayListSheetContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayListSheetContent() {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler(enabled = showPlayListSheet) {
        scope.launch {
            sheetState.hide()
            showPlayListSheet = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            showPlayListSheet = false
        },
        sheetState = sheetState,
        containerColor = Color.Transparent
    ) {
        CpnCurrentPlayList()
    }
}
