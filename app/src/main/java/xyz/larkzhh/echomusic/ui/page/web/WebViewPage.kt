package xyz.larkzhh.echomusic.ui.page.web

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import xyz.larkzhh.echomusic.compose.CommonTopAppBar

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewPage(url: String) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        CommonTopAppBar(title = "网页")
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadsImagesAutomatically = true
                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                    loadUrl(url)
                }
            },
            update = { webView ->
                if (webView.url != url) {
                    webView.loadUrl(url)
                }
            }
        )
    }
}
