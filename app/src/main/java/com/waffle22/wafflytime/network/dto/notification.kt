package com.waffle22.wafflytime.network.dto

import com.squareup.moshi.Json

data class Notification(
    @Json(name = "totalPages") val totalPages: Int,
    @Json(name = "totalElements") val totalElements: Int,
    @Json(name = "size") val size: Int,
    @Json(name = "number") val number: Int,
    @Json(name = "last") val last: Boolean,
    @Json(name = "pageable") val pageable: Pageable,
    @Json(name = "sort") val sort: PageableSorted,
    @Json(name = "content") val notifications: List<NotificationData>
)

data class NotificationData(
    @Json(name = "notificationId") val notificationId: Int,
    @Json(name = "notificationType") val notificationType: String,
    @Json(name = "content") val notificationContent: String,
    @Json(name = "isRead") val isRead: Boolean,
    @Json(name = "contentCreatedAt") val notificationCreatedAt: TimeDTO,
    @Json(name = "info") val notificationInfo: NotificationInfo
)

data class NotificationInfo(
    @Json(name = "boardId") val boardId: Int,
    @Json(name = "boardTitle") val boardTitle: String,
    @Json(name = "postId") val postId: Int,
    @Json(name = "chatId") val chatId: Int?
)

data class Chat(
    val Name: String,
    val content: String,
    val date: String
)
