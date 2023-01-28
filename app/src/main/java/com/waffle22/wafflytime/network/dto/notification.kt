package com.waffle22.wafflytime.network.dto

import com.squareup.moshi.Json

// 알림 관련
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

// 채팅관련
//First Chat 관련
data class NewChatRequest(
    @Json(name = "isAnonymous") val isAnonymous: Boolean,
    @Json(name = "content") val content: String
)

data class NewChatResponse(
    @Json(name = "new") val new: Boolean,
    @Json(name = "chatInfo") val chatInfo: ChatSimpleInfo,
    @Json(name = "systemMessageInfo") val systemMessageInfo: MessageInfo,
    @Json(name = "firstMessageInfo") val firstMessageInfo: MessageInfo
)

data class ChatSimpleInfo(
    @Json(name = "id") val id: Long,
    @Json(name = "target") val target: String,
    @Json(name = "recentMessage") val recentMessage: String,
    @Json(name = "unread") val unread: Int,
    @Json(name = "blocked") val blocked: Boolean
)

data class MessageInfo(
    @Json(name = "sentAt") val sentAt: TimeDTO,
    @Json(name = "received") val received: Boolean,
    @Json(name = "content") val content: String
)

data class Chat(
    val Name: String,
    val content: String,
    val date: String
)
