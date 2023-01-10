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
    private val _authState = MutableStateFlow<String>("StandBy")
    val authState: StateFlow<String> = _authState

    fun resetAuthState(){
        _authState.value = "StandBy"
    }

    fun login(id: String, password: String){
        viewModelScope.launch {
            try{
                val response: Response<TokenContainer> = wafflyApiService.basicLogin(LoginRequest(id, password))
                when (response.code().toString()){
                    "200" -> {
                        authStorage.setAuthInfo(response.body()!!.accessToken, response.body()!!.refreshToken)
                        _authState.value = "LoginOk"
                    }
                    "404" -> {
                        _authState.value = "LoginFailed"
                    }
                    "500" -> {
                        _authState.value = "500Error"
                    }
                }
            } catch (e:java.lang.Exception) {
                Log.d("debug",e.toString())
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