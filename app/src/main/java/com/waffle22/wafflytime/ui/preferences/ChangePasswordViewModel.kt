package com.waffle22.wafflytime.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.ChangePasswordRequest
import com.waffle22.wafflytime.network.dto.UserDTO
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.SlackState
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response

class ChangePasswordViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage,
    private val moshi: Moshi
): ViewModel() {

    private val _state = MutableStateFlow(SlackState<Any>("0", null, null, null))
    val state: StateFlow<SlackState<Nothing>> = _state

    fun changePassword(newPassword: String, oldPassword: String, confirmPassword: String) {
        _state.value = SlackState<Any>("0", null, null, null)

        if(newPassword != confirmPassword) {
            _state.value = SlackState<Any>("-2", null, "새 비밀번호의 오타를 확인해 주세요.", null)
        }
        else {
            viewModelScope.launch {
                val response: Response<UserDTO> =
                    wafflyApiService.changePassword(ChangePasswordRequest(oldPassword, newPassword))
                try {
                    if (response.isSuccessful) {
                        _state.value = SlackState<Any>(response.code().toString(), null, null, null)
                    } else {
                        val errorResponse = HttpException(response).parseError(moshi)!!
                        _state.value = SlackState<Any>(
                            errorResponse.statusCode,
                            errorResponse.errorCode,
                            errorResponse.message,
                            null
                        )
                    }
                } catch (e: Exception) {
                    _state.value = SlackState<Any>("-1", null, "System Corruption", null)
                }
            }
        }

    }
}