package xyz.larkzhh.echomusic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.ViewStateMutableStateFlow
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.SearchResult
import xyz.larkzhh.echomusic.model.SearchSong
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val api: NeteaseApi,
) : BaseViewStateViewModel() {

    val searchResultState = ViewStateMutableStateFlow<SearchResult>()

    var keyword by mutableStateOf("")
    var songs by mutableStateOf<List<SearchSong>>(emptyList())
        private set

    fun getSearch(input: String) {
        keyword = input
        if (input.isBlank()) {
            songs = emptyList()
            return
        }
        launch(searchResultState, handleSuccessBlock = { result ->
            songs = result.result?.songs ?: emptyList()
        }) {
            api.getSearch(input)
        }
    }
}

