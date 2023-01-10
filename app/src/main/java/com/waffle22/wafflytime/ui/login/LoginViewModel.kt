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
    /*
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
    */
}