package com.waffle22.wafflytime.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.LoginRequest
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.SlackState
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.waffle22.wafflytime.network.dto.ChangeNicknameRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

// TODO: Add StateFlow Enum
// Todo: Add Response Code Enum

class LoginViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage,
    private val moshi: Moshi
) : ViewModel() {

    // TODO: Change String type to Enum Class!!!
    private val _loginState = MutableStateFlow(SlackState("0",null,null,null))
    val loginState: StateFlow<SlackState<Nothing>> = _loginState
    private var codeHolder: String? = null
    private var providerHolder: String? = null
    private var stateHolder: SlackState<Nothing> = SlackState("0",null,null,null)

    fun initViewModel() {
        codeHolder = null
        providerHolder = null
        stateHolder = SlackState("0",null,null,null)
    }

    fun resetAuthState(){
        _loginState.value = SlackState("0",null,null,null)
    }

    fun login(id: String, password: String) {
        viewModelScope.launch {
            try {
                val response = wafflyApiService.basicLogin(LoginRequest(id, password))
                if (response.isSuccessful) {
                    authStorage.setTokenInfo(
                        response.body()!!.accessToken,
                        response.body()!!.refreshToken
                    )
                    _loginState.value = SlackState("200", null, null, null)
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _loginState.value = SlackState(
                        errorResponse.statusCode,
                        errorResponse.errorCode,
                        errorResponse.message,
                        null
                    )
                }
            } catch (e: java.lang.Exception) {
                _loginState.value = SlackState("-1", null, "System Corruption", null)
            }
        }
    }

    fun socialLogin(provider: String, code: String) {
        viewModelScope.launch {
            codeHolder = code
            providerHolder = provider
            try {
                val response = wafflyApiService.socialLogin(provider,code)
                if (response.isSuccessful) {
                    // 회원가입 필요!!!!
                    if (response.body()!!.needNickName) {
                        stateHolder = SlackState("-2", null, null, null)
                        _loginState.value = SlackState("-2", null, null, null)
                    } else { // 로그인 진행
                        authStorage.setTokenInfo(
                            response.body()!!.authToken!!.accessToken,
                            response.body()!!.authToken!!.refreshToken
                        )
                        stateHolder = SlackState("200", null, null, null)
                        _loginState.value = SlackState("200", null, null, null)
                    }
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    stateHolder = SlackState(
                        errorResponse.statusCode,
                        errorResponse.errorCode,
                        errorResponse.message,
                        null
                    )
                }
            } catch (e: java.lang.Exception) {
                stateHolder = SlackState("-1", null, "System Corruption", null)
                _loginState.value = SlackState("-1", null, "System Corruption", null)
            }
        }


    }

    fun socialSignUp(nickName: String) {
        viewModelScope.launch {
            try {
                val response = wafflyApiService.socialSignUp(providerHolder!!,codeHolder!!,ChangeNicknameRequest(nickName))
                if (response.isSuccessful) {
                    // 로그인 진행
                        authStorage.setTokenInfo(
                            response.body()!!.accessToken,
                            response.body()!!.refreshToken
                        )
                        _loginState.value = SlackState("200", null, null, null)
                    }
                else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _loginState.value = SlackState(
                        errorResponse.statusCode,
                        errorResponse.errorCode,
                        errorResponse.message,
                        null
                    )
                }
            } catch (e: java.lang.Exception) {
                _loginState.value = SlackState("-1", null, "System Corruption", null)
            }
        }


    }


    fun getState() {
        _loginState.value = stateHolder
    }
}
