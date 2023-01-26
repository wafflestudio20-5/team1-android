package com.waffle22.wafflytime.util

import android.content.Context
import androidx.core.content.edit
import com.waffle22.wafflytime.network.dto.UserDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


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
                    refreshToken = sharedPref.getString(RefreshTokenKey, "")!!,
                    null
                )
            }
        )
    val authInfo: StateFlow<AuthInfo?> = _authInfo

    fun setTokenInfo(inAccessToken: String, inRefreshToken: String) {
        sharedPref.edit {
            putString(AccessTokenKey, inAccessToken)
            putString(RefreshTokenKey, inRefreshToken)
        }
        val authInfo = (authInfo.value?.userInfo)?:null
        _authInfo.value = AuthInfo(inAccessToken,inRefreshToken,authInfo)
    }

    fun setUserDtoInfo(userDTO: UserDTO) {
        sharedPref.edit {
            putString(UserLoginIdKey, userDTO.loginId)
            putString(UserSocialEmailKey, userDTO.socialEmail)
            putString(UserUnivEmailKey, userDTO.univEmail)
            putString(UserNickNameKey, userDTO.nickname)
            putString(UserProfileUrlKey, userDTO.profileUrl)
        }
        val accessToken = (authInfo.value?.accessToken)?:""
        val refreshToken = (authInfo.value?.refreshToken)?:""
        _authInfo.value = AuthInfo(accessToken, refreshToken, userDTO)
    }

    fun modifyUserDtoInfo(key: String, value: String) {
        sharedPref.edit {
            putString(key, value)
        }
        _authInfo.value = AuthInfo(
            accessToken = sharedPref.getString(AccessTokenKey, "")!!,
            refreshToken = sharedPref.getString(RefreshTokenKey, "")!!,
            userInfo = UserDTO(
                sharedPref.getString(UserLoginIdKey, "")!!,
                sharedPref.getString(UserSocialEmailKey, "")!!,
                sharedPref.getString(UserUnivEmailKey, "")!!,
                sharedPref.getString(UserNickNameKey, "")!!,
                sharedPref.getString(UserProfileUrlKey, "")!!
            )
        )
    }

    fun clearAuthInfo() {
        sharedPref.edit {
            putString(AccessTokenKey, "")
            putString(RefreshTokenKey, "")
            putString(UserLoginIdKey, "")
            putString(UserSocialEmailKey, "")
            putString(UserUnivEmailKey, "")
            putString(UserNickNameKey, "")
            putString(UserProfileUrlKey, "")
        }
        _authInfo.value = null
    }

    data class AuthInfo(
        val accessToken: String,
        val refreshToken: String,
        val userInfo: UserDTO?
    )

    companion object {
        const val AccessTokenKey = "access_token"
        const val RefreshTokenKey = "refresh_token"

        const val UserLoginIdKey = "user_loginId"
        const val UserSocialEmailKey = "user_socialEmail"
        const val UserUnivEmailKey = "user_univEmail"
        const val UserNickNameKey = "user_nickName"
        const val UserProfileUrlKey = "user_profileUrl"

        const val SharedPreferenceName = "auth_pref"
    }
}