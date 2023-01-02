package com.waffle22.wafflytime.network.dto

import com.squareup.moshi.Json

data class LoginRequest(
    @Json(name = "id") val userId: String,
    @Json(name = "password") val userPassword: String
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)
