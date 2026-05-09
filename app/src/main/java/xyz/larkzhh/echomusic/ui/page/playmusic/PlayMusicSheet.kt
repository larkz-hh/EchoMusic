package xyz.larkzhh.echomusic.ui.page.playmusic

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.larkzhh.echomusic.core.MusicPlayController
import xyz.larkzhh.echomusic.ui.page.playmusic.component.CpnPlayMusic
import xyz.larkzhh.echomusic.viewmodel.PlayMusicViewModel
import kotlinx.coroutines.launch



@Composable
fun PlayMusicSheet() {
    if (MusicPlayController.showPlayMusicSheet) {
        PlayMusicSheetContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayMusicSheetContent() {
    val viewModel: PlayMusicViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler(enabled = true) {
        scope.launch {
            sheetState.hide()
            viewModel.lastSheetDiskRotateAngleForSnap = 0f
            viewModel.sheetDiskRotate.snapTo(0f)
            viewModel.sheetDiskRotate.stop()
            MusicPlayController.showPlayMusicSheet = false
            MusicPlayController.showCpnBottomMusicPlay = true
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                viewModel.lastSheetDiskRotateAngleForSnap = 0f
                viewModel.sheetDiskRotate.snapTo(viewModel.lastSheetDiskRotateAngleForSnap)
                viewModel.sheetDiskRotate.stop()
                MusicPlayController.showPlayMusicSheet = false
                MusicPlayController.showCpnBottomMusicPlay = true
            }
        },
        sheetState = sheetState,
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(0.dp),
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        tonalElevation = 0.dp,
    ) {
        CpnPlayMusic {
            scope.launch {
                sheetState.hide()
                viewModel.lastSheetDiskRotateAngleForSnap = 0f
                viewModel.sheetDiskRotate.snapTo(0f)
                viewModel.sheetDiskRotate.stop()
                MusicPlayController.showPlayMusicSheet = false
                MusicPlayController.showCpnBottomMusicPlay = true
            }
        }
    }
}
