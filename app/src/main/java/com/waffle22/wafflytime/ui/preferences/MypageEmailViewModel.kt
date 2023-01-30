package com.waffle22.wafflytime.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.EmailCode
import com.waffle22.wafflytime.network.dto.EmailRequest
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.SlackState
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response

class MypageEmailViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage,
    private val moshi: Moshi
): ViewModel() {

    private val _emailState = MutableStateFlow(SlackState("0", null, null, null))
    val emailState: StateFlow<SlackState<Nothing>> = _emailState
    private val _codeState = MutableStateFlow(SlackState("0", null, null, null))
    val codeState: StateFlow<SlackState<Nothing>> = _codeState
    private lateinit var email: String
    private lateinit var code: String

    fun isVerified(): Boolean {
        return authStorage.authInfo.value?.userInfo?.univEmail?.isNotEmpty() ?: false
    }

    fun submitEmail(emailInput: String) {
        _emailState.value = SlackState("0", null, null, null)
        viewModelScope.launch {
            try {
                email = emailInput
                val response: Response<EmailCode> = wafflyApiService.emailAuth(EmailRequest(email))
                if (response.isSuccessful){
                    code = response.body()!!.emailCode
                    _emailState.value = SlackState(response.code().toString(),null,null,null)
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _emailState.value = SlackState(errorResponse.statusCode, errorResponse.errorCode, errorResponse.message, null)
                }
            } catch (e:java.lang.Exception) {
                _emailState.value = SlackState("-1",null,"System Corruption", null)
            }
        }
    }

    fun checkCode(codeInput: String) {
        _codeState.value = SlackState("0", null, null, null)
        viewModelScope.launch {
            try {
                if (codeInput == code) {
                    val response = wafflyApiService.emailPatch(EmailRequest(email))
                    if (response.isSuccessful){
                        authStorage.modifyUserDtoInfo(AuthStorage.UserUnivEmailKey, email)
//                        authStorage.setTokenInfo(response.body()!!.accessToken, response.body()!!.refreshToken)
                        _codeState.value = SlackState(response.code().toString(),null,null, null)
                    } else {
                        val errorResponse = HttpException(response).parseError(moshi)!!
                        _codeState.value = SlackState(errorResponse.statusCode, errorResponse.errorCode, errorResponse.message, null)
                    }
                } else {
                    _codeState.value = SlackState("-2",null,"코드가 일치하지 않습니다.", null)
                }
            } catch (e:java.lang.Exception) {
                _codeState.value = SlackState("-1",null,"System Corruption", null)
            }
        }
    }
}