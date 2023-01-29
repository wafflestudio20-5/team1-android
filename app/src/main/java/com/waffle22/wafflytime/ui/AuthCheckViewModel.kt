package com.waffle22.wafflytime.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.LoginRequest
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.SlackState
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthCheckViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage,
    private val moshi: Moshi
): ViewModel() {
    private val _authState = MutableStateFlow(SlackState("0", null, null, null))
    val authState: StateFlow<SlackState<Nothing>> = _authState

    fun resetAuthState(){
        _authState.value = SlackState("0",null,null,null)
    }

    fun checkAuth(){
        viewModelScope.launch {
            try {
                val responseUserInfo = wafflyApiService.getUserInfo()
                if (responseUserInfo.isSuccessful){
                    authStorage.setUserDtoInfo(responseUserInfo.body()!!)
                    _authState.value = SlackState("200",null,null,null)
                } else {
                    authStorage.clearAuthInfo()
                    val errorResponse = HttpException(responseUserInfo).parseError(moshi)!!
                    _authState.value = SlackState(errorResponse.statusCode,errorResponse.errorCode,errorResponse.message, null)
                }
            } catch (e:java.lang.Exception) {
                authStorage.clearAuthInfo()
                _authState.value = SlackState("-1",null,"System Corruption",null)
            }
        }
    }
}