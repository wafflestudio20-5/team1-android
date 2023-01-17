package com.waffle22.wafflytime.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.LoginRequest
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

// TODO: Add StateFlow Enum
// Todo: Add Response Code Enum

class SignUpViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage,
    private val moshi: Moshi
): ViewModel() {

    // TODO: Change String type to Enum Class!!!
    private val _signUpState = MutableStateFlow<StateStorage>(StateStorage("0",null,null))
    val signUpState: StateFlow<StateStorage> = _signUpState

    fun resetSignUpState(){
        _signUpState.value = StateStorage("0",null,null)
    }

    fun signUp(id: String, password: String, nickName: String){
        viewModelScope.launch {
            try {
                val response: Response<TokenContainer> = wafflyApiService.signUp(SignUpRequest(id, password, nickName))
                if (response.isSuccessful) {
                    authStorage.setAuthInfo(response.body()!!.accessToken, response.body()!!.refreshToken)
                    _signUpState.value = StateStorage("200",null,null)
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _signUpState.value = StateStorage(errorResponse.statusCode, errorResponse.errorCode, errorResponse.message)
                }
            } catch (e:java.lang.Exception) {
                _signUpState.value = StateStorage("-1",null,"System Corruption")
            }
        }
    }
}