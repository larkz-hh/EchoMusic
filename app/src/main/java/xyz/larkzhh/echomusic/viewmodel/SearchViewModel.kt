package xyz.larkzhh.echomusic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import xyz.larkzhh.echomusic.core.SearchHistoryStorage
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.ViewStateMutableStateFlow
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.HotSearchResult
import xyz.larkzhh.echomusic.model.SearchResult
import xyz.larkzhh.echomusic.model.SearchSong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val api: NeteaseApi,
) : BaseViewStateViewModel() {

    val searchResultState = ViewStateMutableStateFlow<SearchResult>()
    val hotSearchState = ViewStateMutableStateFlow<HotSearchResult>()

    var keyword by mutableStateOf("")
    var songs by mutableStateOf<List<SearchSong>>(emptyList())
        private set
    var hotWords by mutableStateOf<List<String>>(emptyList())
        private set
    var history by mutableStateOf<List<String>>(emptyList())
        private set
    var suggestions by mutableStateOf<List<String>>(emptyList())
        private set

    private var suggestJob: Job? = null

    init {
        history = SearchHistoryStorage.load()
    }

    fun removeHistory(keyword: String) {
        SearchHistoryStorage.remove(keyword)
        history = SearchHistoryStorage.load()
    }

    fun clearHistory() {
        SearchHistoryStorage.clear()
        history = emptyList()
    }

    fun loadHotSearch() {
        launch(hotSearchState, handleSuccessBlock = { result ->
            hotWords = result.data.orEmpty()
                .map { it.searchWord }
                .filter { it.isNotBlank() }
                .take(10)
        }) {
            api.getHotSearch()
        }
    }

    fun suggest(input: String) {
        suggestJob?.cancel()
        if (input.isBlank()) {
            suggestions = emptyList()
            return
        }
        suggestJob = viewModelScope.launch {
            delay(250)
            runCatching { api.getSearchSuggest(input) }
                .onSuccess { result ->
                    suggestions = result.result?.allMatch.orEmpty()
                        .map { it.keyword }
                        .filter { it.isNotBlank() }
                        .distinct()
                        .take(10)
                }
                .onFailure { suggestions = emptyList() }
        }
    }

    fun getSearch(input: String) {
        keyword = input
        if (input.isBlank()) {
            songs = emptyList()
            return
        }
        launch(searchResultState, handleSuccessBlock = { result ->
            songs = result.result?.songs ?: emptyList()
            SearchHistoryStorage.add(input)
            history = SearchHistoryStorage.load()
        }) {
            api.getSearch(input)
        }
    }
}
