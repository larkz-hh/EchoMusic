package xyz.larkzhh.echomusic.ui.page.singer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.compose.CommonTopAppBar
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.model.SingleArtist
import xyz.larkzhh.echomusic.ui.page.singer.component.CpnSingerSideBar
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import xyz.larkzhh.echomusic.viewmodel.SingerViewModel
import kotlinx.coroutines.launch

@Composable
fun SingerPage(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    val viewModel: SingerViewModel = hiltViewModel()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.getSingerList()
    }

    val groupedSingers = remember(viewModel.singers) {
        viewModel.singers
            .groupBy { it.indexKey() }
    }
    val sortedKeys = remember(groupedSingers) {
        groupedSingers.keys.sortedWith(sideBarKeyComparator)
    }
    val rowItems = remember(groupedSingers, sortedKeys) {
        buildList {
            sortedKeys.forEach { key ->
                val singerList = groupedSingers[key].orEmpty()
                if (singerList.isEmpty()) return@forEach
                add(SingerRow.Header(key))
                singerList.forEach { add(SingerRow.Item(it)) }
            }
        }
    }
    val sideBarKeys = sortedKeys.filter { key -> groupedSingers[key].orEmpty().isNotEmpty() }
    val keyToIndex = remember(rowItems) {
        buildMap {
            rowItems.forEachIndexed { index, row ->
                if (row is SingerRow.Header) {
                    put(row.title, index)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize(),
    ) {
        CommonTopAppBar(title = "歌手",
            leftIconResId = R.drawable.ic_drawer_toggle,
            leftClick = {
                scope.launch {
                    if (drawerState.isOpen) {
                        drawerState.close()
                    } else {
                        drawerState.open()
                    }
                }
            })

        if (viewModel.singers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "暂无歌手数据",
                    color = AppColorsProvider.current.secondText,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColorsProvider.current.background)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 40.cdp)
                ) {
                    items(
                        items = rowItems,
                        key = { row ->
                            when (row) {
                                is SingerRow.Header -> "h_${row.title}"
                                is SingerRow.Item -> {
                                    val id = row.singer.id
                                        ?: row.singer.accountId
                                        ?: row.singer.picId
                                    "i_${id}_${row.singer.name}"
                                }
                            }
                        }
                    ) { row ->
                        when (row) {
                            is SingerRow.Header -> SingerHeaderRow(row.title)
                            is SingerRow.Item -> SingerItemRow(row.singer)
                        }
                    }
                }

                CpnSingerSideBar(
                    keys = sideBarKeys,
                    listState = listState,
                    keyToIndex = keyToIndex,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 6.cdp)
                )
            }
        }
    }
}

@Composable
private fun SingerHeaderRow(title: String) {
    Text(
        text = title,
        color = AppColorsProvider.current.secondText,
        fontSize = 18.csp,
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColorsProvider.current.background)
            .padding(horizontal = 20.cdp, vertical = 8.cdp)
    )
}

private val SingerListItemTotalHeight = 150.cdp
private val SingerListAvatarSize = 75.cdp

@Composable
private fun SingerItemRow(singer: SingleArtist) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(SingerListItemTotalHeight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.cdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CommonNetworkImage(
                url = singer.picUrl ?: singer.img1v1Url,
                modifier = Modifier
                    .size(SingerListAvatarSize)
                    .clip(CircleShape)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 14.cdp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = singer.name.orEmpty(),
                    color = AppColorsProvider.current.firstText,
                    fontSize = 26.csp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "单曲 ${singer.musicSize ?: 0}  专辑 ${singer.albumSize ?: 0}",
                    color = AppColorsProvider.current.secondText,
                    fontSize = 20.csp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.cdp)
                )
            }

            singer.alias?.firstOrNull()?.takeIf { it.isNotBlank() }?.let { alias ->
                Text(
                    text = alias,
                    color = AppColorsProvider.current.primary,
                    fontSize = 16.csp,
                    modifier = Modifier.padding(start = 8.cdp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.cdp + SingerListAvatarSize + 14.cdp, end = 20.cdp),
            thickness = 0.5.cdp,
            color = AppColorsProvider.current.secondText.copy(alpha = 0.2f)
        )
    }
}

private sealed interface SingerRow {
    data class Header(val title: String) : SingerRow
    data class Item(val singer: SingleArtist) : SingerRow
}

private fun SingleArtist.indexKey(): String {
    val source = name?.takeIf { it.isNotBlank() }
        ?: alias?.firstOrNull()?.takeIf { it.isNotBlank() }
        ?: ""
    val first = source.firstOrNull()?.uppercaseChar() ?: '#'
    return if (first in 'A'..'Z') first.toString() else "#"
}

private val sideBarKeyComparator = Comparator<String> { a, b ->
    fun rank(key: String): Int {
        if (key.length != 1) return 999
        val c = key[0]
        return when (c) {
            in 'A'..'Z' -> c.code - 'A'.code
            '#' -> 26
            else -> 27
        }
    }
    val ra = rank(a)
    val rb = rank(b)
    if (ra != rb) return@Comparator ra.compareTo(rb)
    a.compareTo(b)
}