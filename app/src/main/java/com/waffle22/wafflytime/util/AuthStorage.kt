package com.waffle22.wafflytime.util

import android.content.Context
import androidx.core.content.edit
import com.waffle22.wafflytime.network.dto.UserDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class AuthStorage(
    context: Context
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
        _authInfo.value = AuthInfo(accessToken, refreshToken)
        sharedPref.edit {
            putString(AccessTokenKey, accessToken)
            putString(RefreshTokenKey, refreshToken)
        }
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