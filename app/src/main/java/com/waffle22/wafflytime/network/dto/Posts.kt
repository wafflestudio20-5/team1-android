package com.waffle22.wafflytime.network.dto

import com.squareup.moshi.Json

data class BoardDTO(    //BoardResponse
    @Json(name = "boardId") val boardId: Long,
    @Json(name = "boardType") val boardType: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "allowAnonymous") val allowAnonymous: Boolean
)

data class ImageRequest(
    @Json(name = "imageId") val imageId: Int,
    @Json(name = "fileName") val fileName: String,
    @Json(name = "description") val description: String
)

data class PostRequest(
    @Json(name = "title") val title: String,
    @Json(name = "contents") val contents: String,
    @Json(name = "isQuestion") val isQuestion: Boolean,
    @Json(name = "isWriterAnonymous") val isWriterAnonymous : Boolean,
    @Json(name = "images") val images: List<ImageRequest>
)

data class ImageResponse(
    @Json(name = "imageId") val imageId: Int,
    @Json(name = "preSignedUrl") val preSignedUrl: String,
    @Json(name = "description") val description: String
)

data class EditPostRequest(
    @Json(name = "title") val title: String,
    @Json(name = "contents") val contents: String,
    @Json(name = "isQuestion") val isQuestion: Boolean,
    @Json(name = "isWriterAnonymous") val isWriterAnonymous : Boolean,
    @Json(name = "images") val images: List<ImageRequest>,
    @Json(name = "deletedImages") val deletedImages: List<String>
)

data class PostResponse( // PostResponse
    @Json(name = "postId") val postId: Long,
    @Json(name = "writerId") val writerId: Long,
    @Json(name = "isWriterAnonymous") val isWriterAnonymous : Boolean,
    @Json(name = "title") val title: String,
    @Json(name = "contents") val contents: String,
    @Json(name = "images") val images: List<ImageResponse>
)

data class CreateBoardResponse(
    @Json(name = "userId") val userId : Long,
    @Json(name = "boardId") val boardId: Long,
    @Json(name = "boardType") val boardType: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "allowAnonymous") val allowAnonymous: Boolean
)

data class DeleteBoardResponse(
    @Json(name = "boardId") val boardId: Long,
    @Json(name = "title") val title: String
)

data class DeletePostResponse(
    @Json(name = "boardId") val boardId: Long,
    @Json(name = "boardTitle") val boardTitle: String,
    @Json(name = "postId") val postId: Long,
    @Json(name = "postTitle") val postTitle: String
)

data class EditPostResponse(
    @Json(name = "postId") val postId: Long,
    @Json(name = "writerId") val writerId: Long,
    @Json(name = "isWriterAnonymous") val isWriterAnonymous : Boolean,
    @Json(name = "title") val title: String,
    @Json(name = "contents") val contents: Boolean
)

