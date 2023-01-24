package com.waffle22.wafflytime.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.StateStorage
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LogoutViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage,
    private val moshi: Moshi
) : ViewModel() {

    private val _logoutState = MutableStateFlow<StateStorage>(StateStorage("0",null,null))
    val logoutState: StateFlow<StateStorage> = _logoutState

    fun logout() {
        _logoutState.value = StateStorage("0", null, null)
        viewModelScope.launch{
            try {
                val response = wafflyApiService.logout()
                if (response.isSuccessful) {
                    authStorage.clearAuthInfo()
                    _logoutState.value = StateStorage("200",null,null)
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _logoutState.value = StateStorage(errorResponse.statusCode,errorResponse.errorCode,errorResponse.message)
                }
            } catch (e:java.lang.Exception) {
                _logoutState.value = StateStorage("-1",null,"System Corruption")
            }
        }
    }
}