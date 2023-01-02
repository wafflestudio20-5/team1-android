package com.waffle22.wafflytime.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffle22.wafflytime.network.WafflyApi
import com.waffle22.wafflytime.network.WafflyLogin
import com.waffle22.wafflytime.network.dto.LoginRequest
import com.waffle22.wafflytime.network.dto.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    lateinit var hello : LoginResponse

    fun login(id: String, password: String){
        viewModelScope.launch {
            try {
                hello = WafflyApi.retrofitService.basicLogin(LoginRequest(id,password))
            } catch (e:java.lang.Exception) {
                hello = LoginResponse("no","no")
            }
            Log.d("hello",hello.accessToken +" " + hello.refreshToken)
        }
    }
}