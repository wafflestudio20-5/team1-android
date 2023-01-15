package com.waffle22.wafflytime.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.EmailCode
import com.waffle22.wafflytime.network.dto.EmailRequest
import com.waffle22.wafflytime.network.dto.SignUpRequest
import com.waffle22.wafflytime.network.dto.TokenContainer
import com.waffle22.wafflytime.util.AuthStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class SignUpEmailViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage
): ViewModel() {

    private val _signUpEmailState = MutableStateFlow<SignUpEmailStatus>(SignUpEmailStatus.StandBy)
    val signUpEmailState: StateFlow<SignUpEmailStatus> = _signUpEmailState
    var code: String? = null

    fun resetSignUpEmailState(){
        _signUpEmailState.value = SignUpEmailStatus.StandBy
    }

    fun signUpEmail(email: String){
        viewModelScope.launch {
            try {
                val response: Response<EmailCode> = wafflyApiService.emailAuth(
                    "Bearer "+authStorage.authInfo.value!!.accessToken, EmailRequest(email)
                )
                when (response.code().toString()){
                    "200" -> {
                        code = response.body()!!.emailCode
                        _signUpEmailState.value = SignUpEmailStatus.RequestOk
                    }
                    "400" -> {
                        _signUpEmailState.value = SignUpEmailStatus.BadRequest
                    }
                    "409" -> {
                        _signUpEmailState.value = SignUpEmailStatus.Conflict
                    }
                    "500" -> {
                        _signUpEmailState.value = SignUpEmailStatus.Error_500
                    }
                }
            } catch (e:java.lang.Exception) {
                _signUpEmailState.value = SignUpEmailStatus.Corruption
            }
        }
    }

    fun signUpCode(userCode: String): Boolean{
        return (userCode == (code ?: null))
    }

}