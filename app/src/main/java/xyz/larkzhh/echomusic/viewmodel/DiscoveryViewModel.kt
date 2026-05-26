package xyz.larkzhh.echomusic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.ViewStateMutableStateFlow
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.DiscoverBannerItem
import xyz.larkzhh.echomusic.model.DiscoverBannerResult
import xyz.larkzhh.echomusic.model.MvFirstItem
import xyz.larkzhh.echomusic.model.MvFirstResult
import xyz.larkzhh.echomusic.model.TopListItem
import xyz.larkzhh.echomusic.model.TopListResult
import xyz.larkzhh.echomusic.model.PersonalizedResult
import xyz.larkzhh.echomusic.ui.page.discovery.component.DiscoveryCuratedPlaylistItem
import xyz.larkzhh.echomusic.ui.page.discovery.component.DiscoveryRankingItem
import xyz.larkzhh.echomusic.ui.page.discovery.component.DiscoveryNewSongItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val api: NeteaseApi,
) : BaseViewStateViewModel() {

    val discoverBannerState = ViewStateMutableStateFlow<DiscoverBannerResult>()

    var banners by mutableStateOf<List<DiscoverBannerItem>>(emptyList())
        private set

    val personalizedState = ViewStateMutableStateFlow<PersonalizedResult>()

    var curatedPlaylists by mutableStateOf<List<DiscoveryCuratedPlaylistItem>>(emptyList())
        private set

    val mvFirstState = ViewStateMutableStateFlow<MvFirstResult>()

    var newSongDiskItems by mutableStateOf<List<DiscoveryNewSongItem>>(emptyList())
        private set

    val toplistState = ViewStateMutableStateFlow<TopListResult>()

    var rankingItems by mutableStateOf<List<DiscoveryRankingItem>>(emptyList())
        private set

    fun getDiscoverBanner() {
        launch(discoverBannerState, handleSuccessBlock = { result ->
            banners = result.banners ?: emptyList()
        }) {
            api.getDiscoverBanner()
        }
    }

    fun getPersonalized() {
        launch(personalizedState, handleSuccessBlock = { result ->
            curatedPlaylists = result.toCuratedPlaylistItems()
        }) {
            api.getPersonalized()
        }
    }

    fun getMvFirst(limit: Int = 9) {
        launch(mvFirstState, handleSuccessBlock = { result ->
            newSongDiskItems = result.data?.mapNotNull { it.toDiscoveryNewSongItem() } ?: emptyList()
        }) {
            api.getMvFirst(limit = limit)
        }
    }

    fun getToplist(t: String = "0") {
        launch(toplistState, handleSuccessBlock = { result ->
            val apiItems = result.list.orEmpty()
            val gradients = RANKING_GRADIENTS
            rankingItems = apiItems
                .take(4)
                .mapIndexed { index, src ->
                    val title = src.name?.takeIf { it.isNotBlank() } ?: "榜单"
                    DiscoveryRankingItem(
                        title = title,
                        playlistId = src.id,
                        playlistName = src.name,
                        coverImgUrl = src.coverImgUrl,
                        description = src.description,
                        playCount = src.playCount,
                        gradient = gradients.getOrNull(index) ?: gradients.last(),
                        topSongs = src.topSongsPreview(),
                    )
                }
        }) {
            api.getTopList(t = t)
        }
    }
}

private fun PersonalizedResult.toCuratedPlaylistItems(): List<DiscoveryCuratedPlaylistItem> {
    val fromList = result?.mapNotNull { rec ->
        val cover = rec.picUrl ?: rec.coverImgUrl
        val title = rec.name
        if (!cover.isNullOrBlank() && !title.isNullOrBlank()) {
            DiscoveryCuratedPlaylistItem(
                playlistId = rec.id,
                cover = cover,
                title = title,
                playCount = rec.playCount,
                description = null,
            )
        } else {
            null
        }
    }.orEmpty()
    if (fromList.isNotEmpty()) return fromList

    val d = data ?: return emptyList()
    val cover = d.cover ?: return emptyList()
    val title = d.name ?: return emptyList()
    return listOf(
        DiscoveryCuratedPlaylistItem(
            playlistId = d.id,
            cover = cover,
            title = title,
            playCount = d.playCount,
            description = d.desc ?: d.briefDesc,
        )
    )
}

private fun MvFirstItem.toDiscoveryNewSongItem(): DiscoveryNewSongItem? {
    val coverUrl = cover ?: return null
    val songName = name ?: return null
    val artist = artistName?.takeIf { it.isNotBlank() }
        ?: artists?.mapNotNull { it.name?.takeIf { n -> n.isNotBlank() } }
            ?.joinToString(" / ")
            ?.takeIf { it.isNotBlank() }
        ?: "未知"
    return DiscoveryNewSongItem(
        cover = coverUrl,
        name = songName,
        artist = artist,
        mvid = id,
    )
}

private fun TopListItem.topSongsPreview(): String {
    val firstList = tracks
        ?.mapNotNull { it.first }
        ?.map { it.trim() }
        ?.filter { it.isNotBlank() }
        .orEmpty()
    if (firstList.isNotEmpty()) {
        return firstList.take(3).joinToString("、")
    }
    val secondList = tracks
        ?.mapNotNull { it.second }
        ?.map { it.trim() }
        ?.filter { it.isNotBlank() }
        .orEmpty()
    return if (secondList.isNotEmpty()) secondList.take(3).joinToString("、") else "暂无"
}

private val RANKING_GRADIENTS: List<List<Color>> = listOf(
    listOf(Color(0xFFE83D5A), Color(0xFFB32E46)),
    listOf(Color(0xFF2D86FF), Color(0xFF4F67FF)),
    listOf(Color(0xFFB56BFF), Color(0xFF7A43FF)),
    listOf(Color(0xFF22C7A7), Color(0xFF1E8B7A)),
)
