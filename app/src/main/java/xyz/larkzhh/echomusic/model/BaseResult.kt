package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep
import java.io.Serializable


@Keep
open class BaseResult(
    open var code: Int? = 0,
    open var message: String? = null,
) : Serializable {
    open fun resultOk(): Boolean {
        return code == 200
    }
}