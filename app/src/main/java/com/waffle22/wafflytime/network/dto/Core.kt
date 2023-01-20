package com.waffle22.wafflytime.network.dto

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
    val statusCode: Int?,
    val message: String?,
)