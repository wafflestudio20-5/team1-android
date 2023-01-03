package com.waffle22.wafflytime.network.dto

import com.squareup.moshi.Json

data class LoginRequest(
    @Json(name = "username") val userId: String,
    @Json(name = "password") val userPassword: String
)

data class SignUpRequest(
    @Json(name = "username") val userId: String,
    @Json(name = "password") val userPassword: String
)

data class AccessTokenContainer(
    @Json(name = "accessToken") val accessToken: String
)