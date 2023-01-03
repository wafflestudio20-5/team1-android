package com.waffle22.wafflytime.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffle22.wafflytime.network.WafflyApi
import com.waffle22.wafflytime.network.dto.*
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    lateinit var accessToken : AccessTokenContainer

    fun login(id: String, password: String){
        viewModelScope.launch {
            try {
                accessToken = WafflyApi.retrofitService.basicLogin(LoginRequest(id,password))
            } catch (e:java.lang.Exception) {
                accessToken = AccessTokenContainer("no")
            }
            Log.d("hello",accessToken.accessToken)
        }
    }

    fun signUp(id: String, password: String){
        viewModelScope.launch {
            try {
                accessToken = WafflyApi.retrofitService.signUp(SignUpRequest(id,password))
            } catch (e:java.lang.Exception) {
                accessToken = AccessTokenContainer("no")
            }
            Log.d("hello",accessToken.accessToken)
        }
    }
}