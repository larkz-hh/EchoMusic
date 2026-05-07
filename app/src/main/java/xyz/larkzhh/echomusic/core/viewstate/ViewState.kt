package xyz.larkzhh.echomusic.core.viewstate


sealed class ViewState<out T> {
    object Loading : ViewState<Nothing>()
    data class Success<T>(val data: T) : ViewState<T>()
    object Empty : ViewState<Nothing>()
    data class Fail(val errorCode: String, val errorMsg: String) : ViewState<Nothing>()
    data class Error(val exception: Throwable) : ViewState<Nothing>()
}