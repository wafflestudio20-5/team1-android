package com.waffle22.wafflytime.ui.login


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    fun login(id: String, password: String){
        viewModelScope.launch {
            /*
            try {
                hello = WafflyApi.retrofitService.basicLogin(LoginRequest(id,password))
            } catch (e:java.lang.Exception) {
                hello = LoginResponse("no","no")
            }
            Log.d("hello",hello.accessToken +" " + hello.refreshToken)
            */
        }
    }

    fun signUp(id: String, password: String){
        viewModelScope.launch {
            /*
            try {
                hihi = WafflyApi.retrofitService.signUp(SignUpRequest(id,password))
            } catch (e:java.lang.Exception) {
                hihi = SignUpResponse("no","no")
            }
            Log.d("hello",hihi.accessToken +" " + hihi.refreshToken)
            */
        }
    }
}