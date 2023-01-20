package com.waffle22.wafflytime.ui.mainpage

import android.util.Log
import androidx.lifecycle.ViewModel
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.util.AuthStorage

class MainHomeViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage
): ViewModel() {

    fun isLogin(): Boolean {
        Log.d("debug",authStorage.authInfo.value.toString())
        return authStorage.authInfo.value != null
    }

    fun logOut() {
        authStorage.clearAuthInfo()
    }
}