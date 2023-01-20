package com.waffle22.wafflytime.ui.login

<<<<<<< HEAD

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
=======
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.LoginRequest
import com.waffle22.wafflytime.network.dto.SignUpRequest
import com.waffle22.wafflytime.network.dto.TokenContainer
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.StateStorage
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
>>>>>>> 84f1f575a43063fb3394bb769aaf08e2515303ca
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response

// TODO: Add StateFlow Enum
// Todo: Add Response Code Enum

class LoginViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage,
    private val moshi: Moshi
) : ViewModel() {

    // TODO: Change String type to Enum Class!!!
    private val _loginState = MutableStateFlow<StateStorage>(StateStorage("0",null,null))
    val loginState: StateFlow<StateStorage> = _loginState

    fun resetAuthState(){
        _loginState.value = StateStorage("0",null,null)
    }

    fun login(id: String, password: String){
        viewModelScope.launch {
            try {
                val response = wafflyApiService.basicLogin(LoginRequest(id, password))
                if (response.isSuccessful) {
                    authStorage.setTokenInfo(response.body()!!.accessToken, response.body()!!.refreshToken)
                    _loginState.value = StateStorage("200",null,null)
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _loginState.value = StateStorage(errorResponse.statusCode,errorResponse.errorCode,errorResponse.message)
                }

            } catch (e:java.lang.Exception) {
                _loginState.value = StateStorage("-1",null,"System Corruption")
            }
        }
    }

    fun kakaoSocialLogin(context : Context) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("login", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i("login", "카카오계정으로 로그인 성공 ${token.accessToken}")
            }
        }

// 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e("login", "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    Log.i("login", "카카오톡으로 로그인 성공 ${token.accessToken}")
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }

    }
}