package xyz.larkzhh.echomusic.viewmodel

import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.ViewStateMutableStateFlow
import xyz.larkzhh.echomusic.model.PlaylistBean
import xyz.larkzhh.echomusic.model.SongBean
import xyz.larkzhh.echomusic.model.SongDetailResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PlayListViewModel @Inject constructor(private val api : NeteaseApi) : BaseViewStateViewModel() {

    lateinit var playlistBean: PlaylistBean

    val songDetailResult = ViewStateMutableStateFlow<SongDetailResult>()
    val songList = mutableListOf<SongBean>()

    fun getSongDetail() {
        launch(songDetailResult, handleSuccessBlock = {
            songList.addAll(it.songs)
        }) {
            val playlistDetailResult = api.getPlaylistDetail(playlistBean.id)
            val trackIdBeans = playlistDetailResult.playlist.trackIds
            val ids = trackIdBeans?.joinToString(",") { it.id.toString() } ?: ""
            api.getSongDetail(ids)
        }
    }

}