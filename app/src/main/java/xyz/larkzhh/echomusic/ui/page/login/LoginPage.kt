package xyz.larkzhh.echomusic.ui.page.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.ViewStateLoadingDialogComponent
import xyz.larkzhh.echomusic.core.viewstate.ViewStateMutableStateFlow
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.LoginResult
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import xyz.larkzhh.echomusic.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@Deprecated(message = "使用QrcodeLoginPage")
@Composable
fun LoginPage() {
    val viewModel: LoginViewModel = hiltViewModel()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginByMD5 by remember { mutableStateOf(false) }

    ViewStateLoadingDialogComponent(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.primary),
        viewStateFlow = viewModel.loginResult,
        successBlock = {
            EchoNavController.instance.popBackStack()
            EchoNavController.instance.navigate(RouterUrls.HOME)
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            Box {
                Box(
                    Modifier
                        .padding(top = 205.cdp, start = 5.cdp)
                        .size(190.cdp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 200.cdp)
                        .size(200.cdp)
                        .clip(RoundedCornerShape(50)),
                    tint = AppColorsProvider.current.primaryVariant
                )
            }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("请输入用户名") },
                modifier = Modifier
                    .padding(top = 80.cdp)
                    .focusTarget(),
                singleLine = true,
            )

            Box {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("请输入密码") },
                    modifier = Modifier.padding(top = 40.cdp),
                    visualTransformation = PasswordVisualTransformation(),
                )
            }

            Button(
                onClick = {
                    viewModel.login(username, password, loginByMD5)
                },
                modifier = Modifier
                    .padding(start = 80.cdp, end = 80.cdp, top = 80.cdp)
                    .fillMaxWidth()
                    .height(100.cdp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                content = {
                    Text(text = "登陆", fontSize = 36.csp, color = Color.Black)
                }
            )

            Row(
                modifier = Modifier
                    .padding(start = 80.cdp, end = 80.cdp, top = 20.cdp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = loginByMD5,
                    onClick = {
                        loginByMD5 = !loginByMD5
                    },
                    enabled = true,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.White,
                        unselectedColor = Color.White
                    ),
                )
                Text(
                    text = "md5登录",
                    color = Color.White,
                    fontSize = 24.csp,
                    modifier = Modifier.padding(end = 32.cdp)
                )
            }
        }

    }
}


@HiltViewModel
class LoginViewModel @Inject constructor(private val api: NeteaseApi) : BaseViewStateViewModel() {

    val loginResult = ViewStateMutableStateFlow<LoginResult>()

    fun login(username: String, password: String, loginByMD5: Boolean) {
        if (username.isEmpty()) {
            showToast("请输入用户名")
            return
        }
        if (password.isEmpty()) {
            showToast("请输入密码")
            return
        }
//        launch(loginResult) {
//            val result = api.loginByPassword(
//                username,
//                if (loginByMD5) "" else password,
//                if (loginByMD5) MD5Util.encode(password) else ""
//            )
//            AppGlobalData.sLoginResult = result
//            AppGlobalData.sLoginRefreshFlag = !AppGlobalData.sLoginRefreshFlag
//            result
//        }
    }
}
