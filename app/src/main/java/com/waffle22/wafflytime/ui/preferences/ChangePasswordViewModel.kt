package com.waffle22.wafflytime.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.ChangePasswordRequest
import com.waffle22.wafflytime.network.dto.UserDTO
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.StateStorage
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

    private val _state = MutableStateFlow<StateStorage>(StateStorage("0", null, null))
    val state: StateFlow<StateStorage> = _state

    fun changePassword(newPassword: String, oldPassword: String, confirmPassword: String) {
        _state.value = StateStorage("0", null, null)

        if(newPassword != confirmPassword) {
            _state.value = StateStorage("-2", null, "새 비밀번호의 오타를 확인해 주세요.")
        }
        else {
            viewModelScope.launch {
                val response: Response<UserDTO> =
                    wafflyApiService.changePassword(ChangePasswordRequest(oldPassword, newPassword))
                try {
                    if (response.isSuccessful) {
                        _state.value = StateStorage(response.code().toString(), null, null)
                    } else {
                        val errorResponse = HttpException(response).parseError(moshi)!!
                        _state.value = StateStorage(
                            errorResponse.statusCode,
                            errorResponse.errorCode,
                            errorResponse.message
                        )
                    }
                } catch (e: Exception) {
                    _state.value = StateStorage("-1", null, "System Corruption")
                }
            }
        }

    }
}