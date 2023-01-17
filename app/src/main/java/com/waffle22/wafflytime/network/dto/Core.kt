package com.waffle22.wafflytime.network.dto

import com.squareup.moshi.Json
import java.time.LocalDateTime

data class CommentDTO(
    val id: Int,
    val message: String,
    val author: UserDTO,
    val createdAt: LocalDateTime,
)

data class UserDTO(
    val id: Int,
    val username: String
)

data class ErrorDTO(
    @Json(name = "timestamp") val timeStamp: String,
    @Json(name = "status") val statusCode: String,
    @Json(name = "error-code") val errorCode: String,
    @Json(name = "default-message") val message: String,
)