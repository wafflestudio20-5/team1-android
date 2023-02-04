package com.waffle22.wafflytime.network.dto

import com.squareup.moshi.Json

/*
    백엔드 api를 참고하셔서
    data class를 생성하시면 됩니다.
 */

data class LoginRequest(
    @Json(name = "id") val userId: String,
    @Json(name = "password") val userPassword: String
)

data class SignUpRequest(
    @Json(name = "id") val userId: String,
    @Json(name = "password") val userPassword: String,
    @Json(name = "nickname") val userNickName: String
)

data class EmailRequest(
    @Json(name = "email") val email: String
)

data class EmailCodeRequest(
    @Json(name = "code") val code: String
)

data class TokenContainer(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String
)

data class SocialLoginRequest(
    @Json(name="authToken") val authToken: TokenContainer?,
    @Json(name="needNickname") val needNickName: Boolean
)

data class EmailCode(
    @Json(name = "code") val emailCode: String
)

data class SocialLoginRequest(
    @Json(name="authToken") val authToken: AuthToken,
    @Json(name="needNickname") val needNickName: Boolean
)

data class AuthToken(
    val accessToken: String,
    val refreshToken: String
)