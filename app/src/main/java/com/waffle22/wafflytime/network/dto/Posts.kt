package com.waffle22.wafflytime.network.dto

import com.squareup.moshi.Json

data class BoardDTO(
    @Json(name = "boardId") val boardId: Long,
    @Json(name = "boardType") val boardType: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "allowAnonymous") val allowAnonymous: Boolean
)

data class PostDTO( // PostResponse
    @Json(name = "postId") val postId: Long,
    @Json(name = "writerId") val writerId: Long,
    @Json(name = "isWriterAnonymous") val isWriterAnonymous : Boolean,
    @Json(name = "title") val title: String,
    @Json(name = "contents") val contents: Boolean
)

data class PostResponse(
    val result: PostDTO
)

