package com.waffle22.wafflytime.ui.login

import androidx.lifecycle.ViewModel
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.waffle22.wafflytime.network.dto.SignUpRequest
import com.waffle22.wafflytime.network.dto.TokenContainer
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.LoginRequest
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.SlackState
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.content.Context
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
    private val _loginState = MutableStateFlow(SlackState<Any>("0",null,null))
    val loginState: StateFlow<SlackState<Any>> = _loginState

    fun resetAuthState() {
        _loginState.value = SlackState<Any>("0", null, null)
    }

    fun login(id: String, password: String){
        viewModelScope.launch {


            try {
                val response = wafflyApiService.basicLogin(LoginRequest(id, password))
                if (response.isSuccessful) {
                    authStorage.setTokenInfo(response.body()!!.accessToken, response.body()!!.refreshToken)
                    _loginState.value = SlackState<Any>("200",null,null,)
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _loginState.value = SlackState<Any>(errorResponse.statusCode,errorResponse.errorCode,errorResponse.message)
                }
            } catch (e:java.lang.Exception) {
                _loginState.value = SlackState<Any>("-1",null,"System Corruption")
            }
        }
    }


    fun kakaoSocialLogin(context : Context) {

    }
    fun naverSocialLogin() {

    }
    fun googleSocialLogin() {

    }
    fun githubSocialLogin() {

    }

}