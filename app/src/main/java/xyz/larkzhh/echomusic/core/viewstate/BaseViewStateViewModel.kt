package xyz.larkzhh.echomusic.core.viewstate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.larkzhh.echomusic.model.BaseResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


typealias ViewStateMutableStateFlow<T> = MutableStateFlow<ViewState<T>?>
typealias ViewStateStateFlow<T> = StateFlow<ViewState<T>?>

fun <T> ViewStateMutableStateFlow(): ViewStateMutableStateFlow<T> = MutableStateFlow(null)

open class BaseViewStateViewModel : ViewModel() {

    protected fun <T : BaseResult> launch(
        stateFlow: ViewStateMutableStateFlow<T>? = null,
        handleSuccessBlock: ((T) -> Unit)? = null,
        handleFailBlock: ((code: Int?, message: String?) -> Unit)? = null,
        judgeEmpty: ((T) -> Boolean)? = null,
        call: suspend () -> T
    ) : Job {
        return viewModelScope.launch {
            runCatching {
                stateFlow?.value = ViewState.Loading
                call()
            }.onSuccess { result ->
                if (result.resultOk()) {
                    if (judgeEmpty?.invoke(result) == true) {
                        stateFlow?.value = ViewState.Empty
                    } else {
                        handleSuccessBlock?.invoke(result)
                        stateFlow?.value = ViewState.Success(result)
                    }
                } else {
                    handleFailBlock?.invoke(result.code ?: -1, result.message ?: "请求出错")
                    stateFlow?.value = ViewState.Fail(result.code?.toString() ?: "-1", result.message ?: "请求出错")
                }
            }.onFailure { e ->
                stateFlow?.value = ViewState.Error(e)
            }
        }
    }
}
