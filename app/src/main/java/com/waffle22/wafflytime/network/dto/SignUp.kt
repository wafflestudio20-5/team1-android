package com.waffle22.wafflytime.network.dto

import com.squareup.moshi.Json

data class SignUpRequest(
    @Json(name = "id") val userId: String,
    @Json(name = "password") val userPassword: String
)

data class SignUpResponse(
    val accessToken: String,
    val refreshToken: String
)