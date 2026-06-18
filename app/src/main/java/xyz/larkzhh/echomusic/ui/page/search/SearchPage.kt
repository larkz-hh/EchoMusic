package xyz.larkzhh.echomusic.ui.page.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.core.MusicPlayController
import xyz.larkzhh.echomusic.model.Al
import xyz.larkzhh.echomusic.model.Ar
import xyz.larkzhh.echomusic.model.SearchSong
import xyz.larkzhh.echomusic.model.SongBean
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.ui.page.playmusic.PlayListSheet
import xyz.larkzhh.echomusic.ui.page.playmusic.PlayMusicSheet
import xyz.larkzhh.echomusic.ui.page.playmusic.component.CpnBottomPlayMusic
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import xyz.larkzhh.echomusic.viewmodel.SearchViewModel

@Composable
fun SearchPage() {
    val viewModel: SearchViewModel = hiltViewModel()
    var keyword by remember { mutableStateOf("") }
    var hasSearched by remember { mutableStateOf(false) }
    val resultList = viewModel.songs
    val playList = resultList.map { it.toSongBean() }

    LaunchedEffect(Unit) {
        viewModel.loadHotSearch()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(AppColorsProvider.current.background)) {
            SearchTopBar(
                keyword = keyword,
                onKeywordChange = {
                    keyword = it
                    if (it.isBlank()) {
                        hasSearched = false
                    }
                    viewModel.suggest(it)
                },
                onSearchClick = {
                    if (keyword.isNotBlank()) {
                        hasSearched = true
                        viewModel.getSearch(keyword)
                    }
                }
            )
            if (!hasSearched && keyword.isNotBlank()) {
                SuggestList(viewModel.suggestions, keyword) { word ->
                    keyword = word
                    hasSearched = true
                    viewModel.getSearch(word)
                }
            } else if (!hasSearched) {
                HistorySection(
                    history = viewModel.history,
                    onKeywordClick = { word ->
                        keyword = word
                        hasSearched = true
                        viewModel.getSearch(word)
                    },
                    onRemove = { viewModel.removeHistory(it) },
                    onClear = { viewModel.clearHistory() }
                )
                HotSearchSection(viewModel.hotWords) { word ->
                    keyword = word
                    hasSearched = true
                    viewModel.getSearch(word)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.cdp)) {
                    itemsIndexed(resultList) { index, item ->
                        SearchSongRow(item) {
                            MusicPlayController.setDataSource(playList, index)
                            MusicPlayController.showCpnBottomMusicPlay = false
                            MusicPlayController.showPlayMusicSheet = true
                        }
                    }
                    if (resultList.isEmpty()) {
                        item {
                            Text(
                                text = "暂无搜索结果",
                                color = AppColorsProvider.current.secondText,
                                fontSize = 22.csp,
                                modifier = Modifier.padding(horizontal = 24.cdp, vertical = 20.cdp)
                            )
                        }
                    }
                }
            }
        }

        CpnBottomPlayMusic()
        PlayMusicSheet()
        PlayListSheet()
    }
}

@Composable
private fun SearchSongRow(song: SearchSong, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.cdp, vertical = 10.cdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonNetworkImage(
            url = song.al?.picUrl,
            placeholder = R.drawable.ic_default_disk_cover,
            error = R.drawable.ic_default_disk_cover,
            modifier = Modifier
                .size(88.cdp)
                .clip(RoundedCornerShape(12.cdp))
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 20.cdp)
        ) {
            Text(
                text = song.name.orEmpty(),
                color = AppColorsProvider.current.firstText,
                fontSize = 26.csp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.subtitle(),
                color = AppColorsProvider.current.secondText,
                fontSize = 22.csp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.cdp)
            )
        }
        Text(
            text = song.durationText(),
            color = AppColorsProvider.current.thirdText,
            fontSize = 22.csp
        )
    }
}

@Composable
private fun SearchTopBar(
    keyword: String,
    onKeywordChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 18.cdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonIcon(
            resId = R.drawable.ic_back,
            modifier = Modifier
                .size(54.cdp)
                .clip(RoundedCornerShape(50))
                .clickable { EchoNavController.instance.popBackStack() }
                .padding(8.cdp),
            tint = AppColorsProvider.current.firstIcon
        )
        TextField(
            value = keyword,
            onValueChange = onKeywordChange,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 68.cdp)
                .padding(start = 12.cdp),
            singleLine = true,
            textStyle = TextStyle(fontSize = 14.sp),
            placeholder = {
                Text(
                    "搜索歌曲、歌手、歌单",
                    color = AppColorsProvider.current.secondText,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                CommonIcon(
                    resId = R.drawable.ic_search,
                    modifier = Modifier.size(34.cdp),
                    tint = AppColorsProvider.current.secondIcon
                )
            },
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = AppColorsProvider.current.card,
                unfocusedContainerColor = AppColorsProvider.current.card,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = AppColorsProvider.current.primary
            )
        )
        Text(
            text = "搜索",
            fontSize = 26.csp,
            color = AppColorsProvider.current.firstText,
            modifier = Modifier
                .padding(start = 18.cdp)
                .clip(RoundedCornerShape(8.cdp))
                .clickable { onSearchClick() }
                .padding(horizontal = 8.cdp, vertical = 4.cdp)
        )
    }
}

@Composable
private fun SuggestList(suggestions: List<String>, keyword: String, onClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(suggestions) { _, word ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(word) }
                    .padding(horizontal = 24.cdp, vertical = 14.cdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonIcon(
                    resId = R.drawable.ic_search,
                    modifier = Modifier.size(28.cdp),
                    tint = AppColorsProvider.current.thirdIcon
                )
                Text(
                    text = highlightKeyword(word, keyword),
                    color = AppColorsProvider.current.firstText,
                    fontSize = 26.csp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 16.cdp)
                )
            }
        }
    }
}

@Composable
private fun highlightKeyword(text: String, keyword: String): AnnotatedString {
    val start = if (keyword.isBlank()) -1 else text.indexOf(keyword, ignoreCase = true)
    return buildAnnotatedString {
        if (start < 0) {
            append(text)
            return@buildAnnotatedString
        }
        append(text.substring(0, start))
        withStyle(SpanStyle(color = AppColorsProvider.current.primary)) {
            append(text.substring(start, start + keyword.length))
        }
        append(text.substring(start + keyword.length))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HistorySection(
    history: List<String>,
    onKeywordClick: (String) -> Unit,
    onRemove: (String) -> Unit,
    onClear: () -> Unit
) {
    if (history.isEmpty()) {
        return
    }
    var editing by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    val visible = if (expanded || editing) history else history.take(COLLAPSED_HISTORY_COUNT)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.cdp, vertical = 10.cdp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "搜索历史",
                color = AppColorsProvider.current.firstText,
                fontSize = 28.csp,
                fontWeight = FontWeight.Bold
            )
            Row {
                Text(
                    text = if (editing) "完成" else "编辑",
                    color = AppColorsProvider.current.secondText,
                    fontSize = 24.csp,
                    modifier = Modifier
                        .clickable {
                            editing = !editing
                            if (!editing) {
                                expanded = false
                            }
                        }
                        .padding(horizontal = 8.cdp, vertical = 4.cdp)
                )
                Text(
                    text = "清空",
                    color = AppColorsProvider.current.secondText,
                    fontSize = 24.csp,
                    modifier = Modifier
                        .clickable { showClearDialog = true }
                        .padding(horizontal = 8.cdp, vertical = 4.cdp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.cdp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.cdp),
            verticalArrangement = Arrangement.spacedBy(12.cdp)
        ) {
            visible.forEach { word ->
                HistoryChip(
                    text = word,
                    editing = editing,
                    onClick = { if (!editing) onKeywordClick(word) },
                    onRemove = { onRemove(word) }
                )
            }
            if (!editing && history.size > COLLAPSED_HISTORY_COUNT) {
                HistoryChip(
                    text = if (expanded) "收起" else "展开",
                    editing = false,
                    onClick = { expanded = !expanded },
                    onRemove = {}
                )
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(text = "清空搜索历史") },
            text = { Text(text = "确定清空全部搜索历史吗？") },
            confirmButton = {
                TextButton(onClick = {
                    showClearDialog = false
                    onClear()
                }) {
                    Text(text = "清空")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(text = "取消")
                }
            }
        )
    }
}

@Composable
private fun HistoryChip(
    text: String,
    editing: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.cdp))
            .background(AppColorsProvider.current.card)
            .border(1.cdp, AppColorsProvider.current.divider, RoundedCornerShape(20.cdp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.cdp, vertical = 8.cdp)
    ) {
        Text(
            text = text,
            color = AppColorsProvider.current.firstText,
            fontSize = 22.csp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (editing) {
            Text(
                text = "×",
                color = AppColorsProvider.current.secondText,
                fontSize = 24.csp,
                modifier = Modifier
                    .padding(start = 8.cdp)
                    .clickable(onClick = onRemove)
            )
        }
    }
}

private const val COLLAPSED_HISTORY_COUNT = 8

@Composable
private fun HotSearchSection(words: List<String>, onClick: (String) -> Unit) {
    if (words.isEmpty()) {
        return
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.cdp, vertical = 10.cdp)
    ) {
        Text(
            text = "热门搜索",
            color = AppColorsProvider.current.firstText,
            fontSize = 28.csp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.cdp))
        words.chunked(2).forEachIndexed { rowIndex, pair ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.cdp)) {
                pair.forEachIndexed { columnIndex, word ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onClick(word) }
                            .padding(vertical = 6.cdp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${rowIndex * 2 + columnIndex + 1}",
                            color = AppColorsProvider.current.primary,
                            fontSize = 22.csp,
                            modifier = Modifier.width(36.cdp)
                        )
                        Text(
                            text = word,
                            color = AppColorsProvider.current.secondText,
                            fontSize = 22.csp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private fun SearchSong.subtitle(): String {
    val artists = ar.orEmpty().mapNotNull { it.name }.joinToString("/")
    val album = al?.name.orEmpty()
    return listOf(artists, album).filter { it.isNotBlank() }.joinToString(" · ")
}

private fun SearchSong.durationText(): String {
    val millis = dt ?: 0L
    if (millis <= 0) {
        return ""
    }
    val seconds = millis / 1000
    return "%02d:%02d".format(seconds / 60, seconds % 60)
}

private fun SearchSong.toSongBean(): SongBean = SongBean(
    id = id ?: 0L,
    name = name.orEmpty(),
    al = Al(
        id = al?.id ?: 0L,
        name = al?.name.orEmpty(),
        picUrl = al?.picUrl.orEmpty()
    ),
    ar = ar.orEmpty().map { Ar(id = it.id ?: 0L, name = it.name.orEmpty()) }
)
