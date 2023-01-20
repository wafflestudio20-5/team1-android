package com.waffle22.wafflytime.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.LoginRequest
import com.waffle22.wafflytime.network.dto.SignUpRequest
import com.waffle22.wafflytime.network.dto.TokenContainer
import com.waffle22.wafflytime.util.AuthStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import retrofit2.Response

// TODO: Add StateFlow Enum
// Todo: Add Response Code Enum

class LoginViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage
) : ViewModel() {

    // TODO: Change String type to Enum Class!!!
    private val _loginState = MutableStateFlow<LoginStatus>(LoginStatus.StandBy)
    val loginState: StateFlow<LoginStatus> = _loginState

    fun resetAuthState(){
        _loginState.value = LoginStatus.StandBy
    }

    fun login(id: String, password: String){
        viewModelScope.launch {
            try{
                val response: Response<TokenContainer> = wafflyApiService.basicLogin(LoginRequest(id, password))
                when (response.code().toString()){
                    "200" -> {
                        authStorage.setAuthInfo(response.body()!!.accessToken, response.body()!!.refreshToken)
                        _loginState.value = LoginStatus.LoginOk
                    }
                    "404" -> {
                        _loginState.value = LoginStatus.LoginFailed
                    }
                    "500" -> {
                        _loginState.value = LoginStatus.Error_500
                    }
                }
            } catch (e:java.lang.Exception) {
                _loginState.value = LoginStatus.Corruption
            }
        }
    }
 
    fun refresh(){
        viewModelScope.launch {
            try {
                val response = wafflyApiService.refresh("Bearer "+authStorage.authInfo.value!!.refreshToken)
                val signUpResponse = response.body()
                authStorage.setAuthInfo(signUpResponse!!.accessToken, signUpResponse!!.refreshToken)
                Log.d("debug",response.code().toString())
            } catch (e:java.lang.Exception) {
                Log.d("debug",e.toString())
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