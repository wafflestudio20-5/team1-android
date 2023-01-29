package com.waffle22.wafflytime.network.dto

import com.squareup.moshi.Json

data class ChangeNicknameRequest (
    @Json(name = "nickname") val username: String
)

data class SetProfilePicRequest (
    val fileName: String
)

data class ChangePasswordRequest (
    @Json(name = "oldPassword") val oldPassword: String,
    @Json(name = "newPassword") val newPassword: String
)