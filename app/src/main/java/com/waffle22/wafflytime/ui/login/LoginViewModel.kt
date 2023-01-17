package com.waffle22.wafflytime.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.LoginRequest
import com.waffle22.wafflytime.network.dto.SignUpRequest
import com.waffle22.wafflytime.network.dto.TokenContainer
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.StateStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

// TODO: Add StateFlow Enum
// Todo: Add Response Code Enum

class LoginViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage
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
                when (response.code().toString()){
                    "200" -> {
                        authStorage.setAuthInfo(response.body()!!.accessToken, response.body()!!.refreshToken)
                        _loginState.value = StateStorage("200",null,null)
                    }
                    else -> {
                        /*
                        "404" -> {
                            _loginState.value = StateStorage("404",null,null)
                        }
                         */
                    }
                }

            } catch (e:java.lang.Exception) {
                _loginState.value = StateStorage("-1",null,"System Corruption")
            }
        }
    }
/*
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
 */
}