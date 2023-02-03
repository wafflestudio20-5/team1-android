package com.waffle22.wafflytime.network.dto

import com.squareup.moshi.Json

// 알림 관련
data class Notification(
    @Json(name = "contents") val notifications: List<NotificationData>,
    @Json(name = "cursor") val cursor: Int?,
    @Json(name = "size") val size: Int,
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
    @Json(name = "boardId") val boardId: Long,
    @Json(name = "boardTitle") val boardTitle: String,
    @Json(name = "postId") val postId: Long,
    @Json(name = "chatId") val chatId: Long?
)

