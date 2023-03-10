package com.waffle22.wafflytime.ui.mainpage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.TokenContainer
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response

class MainHomeViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage,
    private val moshi: Moshi
): ViewModel() {

    fun isLogin(): Boolean {
        Log.d("debug",authStorage.authInfo.value.toString())
        return authStorage.authInfo.value != null
    }

    fun logOut() {
        authStorage.clearAuthInfo()
    }
}