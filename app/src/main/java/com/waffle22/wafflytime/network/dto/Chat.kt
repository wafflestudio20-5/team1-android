package com.waffle22.wafflytime.network.dto

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

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
    @Json(name = "recentMessage") val recentMessage: String?,
    @Json(name = "recentTime") val recentTime: TimeDTO?,
    @Json(name = "unread") val unread: Int,
    @Json(name = "blocked") val blocked: Boolean
)

data class MessageInfo(
    @Json(name = "id") val id: Long,
    @Json(name = "sentAt") val sentAt: TimeDTO,
    @Json(name = "received") val received: Boolean,
    @Json(name = "contents") val contents: String
)

data class Chat(
    val Name: String,
    val content: String,
    val date: String
)

data class MessagesPage(
    @Json(name = "content") val content: List<MessageInfo>,
    @Json(name = "pageable") val pageable: Pageable,
    @Json(name = "totalPages") val totalPages: Int,
    @Json(name = "totalElements") val totalElements: Int,
    @Json(name = "last") val last: Boolean,
    @Json(name = "size")  val size: Int,
    @Json(name = "number") val number: Int,
    @Json(name = "sort") val sort: PageableSorted,
    @Json(name = "numberOfElements") val numberOfElements: Int,
    @Json(name = "first") val first: Boolean,
    @Json(name = "empty") val empty: Boolean
)

data class WebSocketClientMessage(
    @Json(name = "chatId") val chatId: Long,
    @Json(name = "contents") val contents: String,
)

data class WebSocketServerMessage(
    @Json(name = "type") val type: String,
)

data class WebChatMessageInfo(
    @Json(name = "chatId") val chatId: Long,
    @Json(name = "sentAt") val sentAt: TimeDTO,
    @Json(name = "received") val received: Boolean,
    @Json(name = "contents") val contents: String,
    @Json(name = "type") val type: String,
)

data class WebChatCreationInfo(
    @Json(name = "chatId") val chatId: Long,
    @Json(name = "target") val target: String,
    @Json(name = "type") val type: String,
)

data class WebChatNeedUpdateInfo(
    @Json(name = "chatId") val chatId: List<Long>,
    @Json(name = "unread") val unread: List<Int>,
    @Json(name = "type") val type: String,
)

data class BlockChatRoomRequest(
    @Json(name = "block") val block: Boolean,
)

data class GetChatListResponse(
    @Json(name = "contents") val contents: List<ChatSimpleInfo>,
    @Json(name = "cursor") val cursor: Long?,
    @Json(name = "size") val size: Long,
)

data class GetMessagesResponse(
    @Json(name = "contents") val contents: List<MessageInfo>,
    @Json(name = "cursor") val cursor: Long?,
    @Json(name = "size") val size: Long,
    @Json(name = "isLast") val isLast: Boolean,
)

data class PutUnreadRequest(
    @Json(name = "chatId") val chatId: List<Long>,
    @Json(name = "unread") val unread: List<Int>,
)