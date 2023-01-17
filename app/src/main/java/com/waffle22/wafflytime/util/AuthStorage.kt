package com.waffle22.wafflytime.util

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.UserDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import kotlin.coroutines.coroutineContext


class AuthStorage(
    private val context: Context
) {
    private val sharedPref =
        context.getSharedPreferences(SharedPreferenceName, Context.MODE_PRIVATE)
    private val _authInfo: MutableStateFlow<AuthInfo?> =
        MutableStateFlow(
            if ((sharedPref.getString(AccessTokenKey, "") ?: "").isEmpty()) {
                null
            } else {
                AuthInfo(
                    accessToken = sharedPref.getString(AccessTokenKey, "")!!,
                    refreshToken = sharedPref.getString(RefreshTokenKey, "")!!
                )
            }
        )
    val authInfo: StateFlow<AuthInfo?> = _authInfo

    fun setAuthInfo(accessToken: String, refreshToken: String) {
        sharedPref.edit {
            putString(AccessTokenKey, accessToken)
            putString(RefreshTokenKey, refreshToken)
        }
        _authInfo.value =
            if (accessToken.isEmpty()) {
                null
            } else {
                AuthInfo(accessToken, refreshToken)
            }
    }

    fun clearAuthInfo() {
        sharedPref.edit {
            putString(AccessTokenKey, "")
            putString(RefreshTokenKey, "")
        }
        _authInfo.value = null
    }

    data class AuthInfo(
        val accessToken: String,
        val refreshToken: String,
    )

    companion object {
        const val AccessTokenKey = "access_token"
        const val RefreshTokenKey = "refresh_token"
        const val UsernameKey = "username"
        const val UserIdKey = "user_id"

        const val SharedPreferenceName = "auth_pref"
    }
}