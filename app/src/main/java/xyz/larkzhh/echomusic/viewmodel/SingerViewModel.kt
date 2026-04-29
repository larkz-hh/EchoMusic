package xyz.larkzhh.echomusic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.ViewStateMutableStateFlow
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.SingleArtist
import xyz.larkzhh.echomusic.model.SingleListResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SingerViewModel @Inject constructor(
    private val api: NeteaseApi,
) : BaseViewStateViewModel() {

    val singerListState = ViewStateMutableStateFlow<SingleListResult>()

    var singers by mutableStateOf<List<SingleArtist>>(emptyList())
        private set

    fun getSingerList(type: String = "-1", area: String = "-1") {
        launch(singerListState, handleSuccessBlock = { result ->
            singers = result.artists.orEmpty()
        }) {
            api.getSingerList(type = type, area = area)
        }
    }
}

