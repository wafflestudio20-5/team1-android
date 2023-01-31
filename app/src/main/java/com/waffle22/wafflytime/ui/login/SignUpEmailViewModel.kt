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
import com.waffle22.wafflytime.util.SlackState
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

    private val _signUpEmailState = MutableStateFlow(SlackState("0",null,null,null))
    val signUpEmailState: StateFlow<SlackState<Nothing>> = _signUpEmailState

    private val _signUpCodeState = MutableStateFlow(SlackState("0",null,null,null))
    val signUpCodeState: StateFlow<SlackState<Nothing>> = _signUpCodeState
    var code: String? = null
    var email: String? = null

    fun resetSignUpEmailState(){
        _signUpEmailState.value = SlackState("0",null,null,null)
    }

    fun signUpEmail(userEmail: String){
        viewModelScope.launch {
            email = userEmail
            try {
                val response: Response<EmailCode> = wafflyApiService.emailAuth(EmailRequest(userEmail))
                if (response.isSuccessful){
                    code = response.body()!!.emailCode
                    _signUpEmailState.value = SlackState("200",null,null,null)
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _signUpEmailState.value = SlackState(errorResponse.statusCode, errorResponse.errorCode, errorResponse.message,null)
                }
            } catch (e:java.lang.Exception) {
                _signUpEmailState.value = SlackState("-1",null,"System Corruption",null)
            }
        }
    }

    fun resetSignUpCodeState(){
        _signUpCodeState.value = SlackState("0",null,null,null)
    }

    fun signUpCode(userCode: String){
        viewModelScope.launch {
            try {
                if (userCode == code) {
                    // 이메일 확인요청 보내기
                    val response = wafflyApiService.emailPatch(EmailRequest(email!!))
                    if (response.isSuccessful){
                        authStorage.setTokenInfo(response.body()!!.accessToken, response.body()!!.refreshToken)
                        _signUpCodeState.value = SlackState("200",null,null,null)
                    } else {
                        val errorResponse = HttpException(response).parseError(moshi)!!
                        _signUpCodeState.value = SlackState(errorResponse.statusCode, errorResponse.errorCode, errorResponse.message,null)
                    }
                } else {
                    _signUpCodeState.value = SlackState("-2",null,"코드가 일치하지 않습니다.",null)
                }
            } catch (e:java.lang.Exception) {
                _signUpCodeState.value = SlackState("-1",null,"System Corruption",null)
            }
        }
    }
    /*
    private suspend fun getUserInfo(): StateStorage{
        try {
            val responseUserInfo = wafflyApiService.getUserInfo()
            if (responseUserInfo.isSuccessful){
                authStorage.setUserDtoInfo(responseUserInfo.body()!!)
                return StateStorage("200",null,null)
            } else {
                val errorResponse = HttpException(responseUserInfo).parseError(moshi)!!
                return StateStorage(errorResponse.statusCode,errorResponse.errorCode,errorResponse.message)
            }
        } catch (e:java.lang.Exception) {
            return StateStorage("-1",null,"System Corruption")
        }
    }
     */
}