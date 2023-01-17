package com.waffle22.wafflytime.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.EmailCode
import com.waffle22.wafflytime.network.dto.EmailRequest
import com.waffle22.wafflytime.network.dto.SignUpRequest
import com.waffle22.wafflytime.network.dto.TokenContainer
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.StateStorage
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response

class SignUpEmailViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage,
    private val moshi: Moshi
): ViewModel() {

    private val _signUpEmailState = MutableStateFlow<StateStorage>(StateStorage("0",null,null))
    val signUpEmailState: StateFlow<StateStorage> = _signUpEmailState

    private val _signUpCodeState = MutableStateFlow<StateStorage>(StateStorage("0",null,null))
    val signUpCodeState: StateFlow<StateStorage> = _signUpCodeState
    var code: String? = null
    var email: String? = null

    fun resetSignUpEmailState(){
        _signUpEmailState.value = StateStorage("0",null,null)
    }

    fun signUpEmail(userEmail: String){
        viewModelScope.launch {
            email = userEmail
            try {
                val response: Response<EmailCode> = wafflyApiService.emailAuth(EmailRequest(userEmail))
                if (response.isSuccessful){
                    code = response.body()!!.emailCode
                    _signUpEmailState.value = StateStorage("200",null,null)
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _signUpEmailState.value = StateStorage(errorResponse.statusCode, errorResponse.errorCode, errorResponse.message)
                }
            } catch (e:java.lang.Exception) {
                _signUpEmailState.value = StateStorage("-1",null,"System Corruption")
            }
        }
    }

    fun resetSignUpCodeState(){
        _signUpCodeState.value = StateStorage("0",null,null)
    }

    fun signUpCode(userCode: String){
        viewModelScope.launch {
            try {
                if (userCode == code) {
                    // 이메일 확인요청 보내기
                    val response = wafflyApiService.emailPatch(EmailRequest(email!!))
                    if (response.isSuccessful){
                        authStorage.setAuthInfo(response.body()!!.accessToken, response.body()!!.refreshToken)
                        _signUpCodeState.value = StateStorage("200",null,null)
                    } else {
                        val errorResponse = HttpException(response).parseError(moshi)!!
                        _signUpCodeState.value = StateStorage(errorResponse.statusCode, errorResponse.errorCode, errorResponse.message)
                    }
                } else {
                    _signUpCodeState.value = StateStorage("-2",null,"코드가 일치하지 않습니다.")
                }
            } catch (e:java.lang.Exception) {
                _signUpCodeState.value = StateStorage("-1",null,"System Corruption")
            }
        }
    }

}