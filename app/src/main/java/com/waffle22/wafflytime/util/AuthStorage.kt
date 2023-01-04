package com.waffle22.wafflytime.util
/*
import android.content.Context
import androidx.core.content.edit
import com.waffle22.wafflytime.network.dto.UserDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
*/
/*
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
                    UserDTO(
                        id = sharedPref.getInt(UserIdKey, -1),
                        username = sharedPref.getString(UsernameKey, "")!!,
                    )
                )
            }
        )
    val authInfo: StateFlow<AuthInfo?> = _authInfo

    fun setAuthInfo(token: String, user: UserDTO) {
        _authInfo.value = AuthInfo(token, user)
        sharedPref.edit {
            putString(AccessTokenKey, token)
            putInt(UserIdKey, user.id)
            putString(UsernameKey, user.username)
        }
    }

    data class AuthInfo(
        val accessToken: String,
        val user: UserDTO,
    )

    companion object {
        const val AccessTokenKey = "access_token"
        const val UsernameKey = "username"
        const val UserIdKey = "user_id"

        const val SharedPreferenceName = "auth_pref"
    }
}
 */