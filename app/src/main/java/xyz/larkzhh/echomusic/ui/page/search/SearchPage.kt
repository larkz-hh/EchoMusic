package xyz.larkzhh.echomusic.ui.page.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.nav.EchoNavController
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

    Column(modifier = Modifier.fillMaxSize().background(AppColorsProvider.current.background)) {
        SearchTopBar(
            keyword = keyword,
            onKeywordChange = { keyword = it },
            onSearchClick = {
                hasSearched = true
                viewModel.getSearch(keyword)
            }
        )
        if (!hasSearched) {
            SearchEntryRow()
            GuessLikeSection()
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.cdp)) {
                items(resultList) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.cdp)
                            .padding(horizontal = 24.cdp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name.orEmpty(),
                                color = AppColorsProvider.current.firstText,
                                fontSize = 24.csp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = item.artists?.firstOrNull()?.name.orEmpty(),
                                color = AppColorsProvider.current.secondText,
                                fontSize = 20.csp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
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
                    "桃德李 Todd Li",
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
private fun SearchEntryRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.cdp, vertical = 14.cdp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SearchEntryItem(R.drawable.ic_mine, "歌手")
        SearchEntryItem(R.drawable.ic_sing, "曲风")
        SearchEntryItem(R.drawable.ic_cloud_country, "专区")
        SearchEntryItem(R.drawable.ic_play_list, "识曲")
    }
}

@Composable
private fun SearchEntryItem(icon: Int, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CommonIcon(
            resId = icon,
            modifier = Modifier.size(34.cdp),
            tint = AppColorsProvider.current.secondIcon
        )
        Text(
            text = title,
            fontSize = 24.csp,
            color = AppColorsProvider.current.firstText,
            modifier = Modifier.padding(start = 8.cdp)
        )
    }
}

@Composable
private fun GuessLikeSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.cdp, vertical = 10.cdp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "猜你喜欢",
                color = AppColorsProvider.current.firstText,
                fontSize = 28.csp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "⟳",
                color = AppColorsProvider.current.secondText,
                fontSize = 24.csp
            )
        }
        Spacer(modifier = Modifier.height(16.cdp))
        GUESS_LIKE.chunked(2).forEach { pair ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.cdp)) {
                Text(
                    text = pair.getOrNull(0) ?: "",
                    modifier = Modifier.weight(1f),
                    fontSize = 22.csp,
                    color = AppColorsProvider.current.secondText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = pair.getOrNull(1) ?: "",
                    modifier = Modifier.weight(1f),
                    fontSize = 22.csp,
                    color = AppColorsProvider.current.secondText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private val GUESS_LIKE = listOf(
    "海屿你", "Lukas Rieger",
    "儿歌多多", "贝乐虎儿歌",
    "郑润泽", "薛之谦",
    "房东的猫", "毛不易",
)

