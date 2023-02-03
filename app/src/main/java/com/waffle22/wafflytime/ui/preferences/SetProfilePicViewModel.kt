package com.waffle22.wafflytime.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.SetProfilePicRequest
import com.waffle22.wafflytime.network.dto.UserDTO
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.SlackState
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Response

class SetProfilePicViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage,
    private val moshi: Moshi
): ViewModel() {

    private val _state = MutableStateFlow(SlackState("0", null, null, null))
    val state: StateFlow<SlackState<Nothing>> = _state

    private val _profileUrl = MutableStateFlow<SlackState<String>>(SlackState("0", null, null, null))
    val profileUrl: StateFlow<SlackState<String>> = _profileUrl

    fun setProfilePic(fileName: String, byteArray: ByteArray) {
        _state.value = SlackState("0", null, null, null)
        viewModelScope.launch {
            try {
                val response: Response<UserDTO> = wafflyApiService.setProfilePic(
                    SetProfilePicRequest(fileName)
                )
                if(response.isSuccessful) {
                    val response2: Response<Unit> = wafflyApiService.uploadImage(response.body()!!.profileUrl!!, byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull()))
                    if(response2.isSuccessful) {
                        _state.value = SlackState(response.code().toString(), null, null, null)
                    }
                    else {
                        _state.value = SlackState(response.code().toString(), null, "Cannot upload picture", null)
                    }
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _state.value = SlackState(errorResponse.statusCode, errorResponse.statusCode, errorResponse.message, null)
                }
            }
            catch (e: Exception) {
                _state.value = SlackState("-1", null, "System Corruption", null)
            }
        }
    }

    fun deleteProfilePic() {
        _state.value = SlackState("0", null, null, null)
        viewModelScope.launch {
            try {
                val response: Response<UserDTO> = wafflyApiService.deleteProfilePic()
                if (response.isSuccessful) {
                    _state.value = SlackState(response.code().toString(), null, null, null)
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _state.value = SlackState(
                        errorResponse.statusCode,
                        errorResponse.statusCode,
                        errorResponse.message,
                        null
                    )
                }
            }
            catch (e: Exception) {
                _state.value = SlackState("-1", null, "System Corruption", null)
            }
        }
    }

    fun getProfileUrl() {
        _profileUrl.value = SlackState("0", null, null, null)
        viewModelScope.launch {
            try {
                val response: Response<UserDTO> = wafflyApiService.getUserInfo()
                if(response.isSuccessful) {
                    _profileUrl.value = SlackState(response.code().toString(),
                        null,
                        null,
                        response.body()!!.profileUrl,
                    )
                }
                else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _profileUrl.value = SlackState(
                        errorResponse.statusCode,
                        errorResponse.errorCode,
                        errorResponse.message,
                        null
                    )
                }
            }
            catch(e: Exception) {
                _profileUrl.value = SlackState("-1", null, "Sytem Corruption", null)
            }
        }
    }
}