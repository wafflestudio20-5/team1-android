package com.waffle22.wafflytime.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.SetProfilePicRequest
import com.waffle22.wafflytime.network.dto.UserDTO
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.StateStorage
import com.waffle22.wafflytime.util.StateValueStorage
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

    private val _state = MutableStateFlow<StateStorage>(StateStorage("0", null, null))
    val state: StateFlow<StateStorage> = _state

    private val _profileUrl = MutableStateFlow<StateValueStorage>(StateValueStorage("0", null, null, null))
    val profileUrl: StateFlow<StateValueStorage> = _profileUrl

    fun setProfilePic(fileName: String, byteArray: ByteArray) {
        _state.value = StateStorage("0", null, null)
        viewModelScope.launch {
            try {
                val response: Response<UserDTO> = wafflyApiService.setProfilePic(
                    SetProfilePicRequest(fileName)
                )
                if(response.isSuccessful) {
                    val response2: Response<Unit> = wafflyApiService.uploadProfilePic(response.body()!!.profileUrl!!, byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull()))
                    if(response2.isSuccessful) {
                        _state.value = StateStorage(response.code().toString(), null, null)
                    }
                    else {
                        _state.value = StateStorage(response.code().toString(), null, "Cannot upload picture")
                    }
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _state.value = StateStorage(errorResponse.statusCode, errorResponse.statusCode, errorResponse.message)
                }
            }
            catch (e: Exception) {
                _state.value = StateStorage("-1", null, "System Corruption")
            }
        }
    }

    fun deleteProfilePic() {
        _state.value = StateStorage("0", null, null)
        viewModelScope.launch {
            try {
                val response: Response<UserDTO> = wafflyApiService.deleteProfilePic()
                if (response.isSuccessful) {
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
            catch (e: Exception) {
                _state.value = StateStorage("-1", null, "System Corruption")
            }
        }
    }

    fun getProfileUrl() {
        _profileUrl.value = StateValueStorage("0", null, null, null)
        viewModelScope.launch {
            try {
                val response: Response<UserDTO> = wafflyApiService.getUserInfo()
                if(response.isSuccessful) {
                    _profileUrl.value = StateValueStorage(response.code().toString(),
                        response.body()!!.profileUrl,
                        null,
                        null
                    )
                }
                else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _profileUrl.value = StateValueStorage(
                        errorResponse.statusCode,
                        null,
                        errorResponse.errorCode,
                        errorResponse.message
                    )
                }
            }
            catch(e: Exception) {
                _profileUrl.value = StateValueStorage("-1", null, null, "Sytem Corruption")
            }
        }
    }
}