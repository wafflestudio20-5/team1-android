package com.waffle22.wafflytime.ui.preferences

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.ChangeNicknameRequest
import com.waffle22.wafflytime.network.dto.UserDTO
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.StateStorage
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

class SetNicknameViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage,
    private val moshi: Moshi
): ViewModel() {

    private val _state = MutableStateFlow<StateStorage>(StateStorage("0", null, null))
    val state: StateFlow<StateStorage> = _state

    suspend fun checkUsername(newUsername: String): Boolean {
        val response: Response<ResponseBody> = wafflyApiService.checkNickname(newUsername)
        return if(response.isSuccessful) {
            false
        } else {
            val errorResponse = HttpException(response).parseError(moshi)!!
            _state.value = StateStorage(errorResponse.statusCode, errorResponse.statusCode, errorResponse.message)
            true
        }
    }

    fun changeUsername(newUsername: String) {
        _state.value = StateStorage("0", null, null)
        viewModelScope.launch {
            try {
                val duplicate = checkUsername(newUsername)
                if(!duplicate) {
                    val response: Response<UserDTO> = wafflyApiService.changeNickname(
                        ChangeNicknameRequest(newUsername)
                    )
                    if (response.isSuccessful) {
                        authStorage.modifyUserDtoInfo(AuthStorage.UserNickNameKey, newUsername)
                        _state.value = StateStorage(response.code().toString(), null, null)
                    } else {
                        val errorResponse = HttpException(response).parseError(moshi)!!
                        _state.value = StateStorage(
                            errorResponse.statusCode,
                            errorResponse.statusCode,
                            errorResponse.message
                        )
                    }
                }
            }
            catch (e: Exception) {
                Log.e("EXCEPTION", e.message.toString())
                _state.value = StateStorage("-1", null, "System Corruption")
            }
        }
    }
}