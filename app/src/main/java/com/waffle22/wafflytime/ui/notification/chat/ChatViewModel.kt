package com.waffle22.wafflytime.ui.notification.chat

import WebSocketManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.*
import com.waffle22.wafflytime.ui.notification.chat.list.ChatListPagingSource
import com.waffle22.wafflytime.ui.notification.chat.room.ChatRoomPagingSource
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.SlackState
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import retrofit2.HttpException

class ChatViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi,
    private val webSocketManager: WebSocketManager
) : ViewModel(), MessageListener {

    private val _chatListState = MutableStateFlow(
        SlackState("0", null, null, null)
    )
    val chatListState: StateFlow<SlackState<Nothing>> = _chatListState
    private val _chatList = MutableStateFlow(listOf<ChatSimpleInfo>())
    val chatList: StateFlow<List<ChatSimpleInfo>> = _chatList
    private var chatListCursor: Long? = null

    var curChat: ChatSimpleInfo? = null
    private set(value) {
        field = value
        value?.let {
            _chatList.value = _chatList.value.map {
                if (it.id == value.id) {
                    value
                } else {
                    it
                }
            }
        }
    }

    private val _messagesState = MutableStateFlow(
        SlackState("0", null, null, null)
    )
    val messagesState: StateFlow<SlackState<Nothing>> = _messagesState
    private val _messageList = MutableStateFlow(listOf<MessageInfo>())
    val messageList: StateFlow<List<MessageInfo>> = _messageList
    private var messageCursor: Long? = null

    private val _blockState = MutableStateFlow(SlackState("0", null, null, null))
    val blockState = _blockState

    fun setupChatRoom(chatId: Long) {
        curChat = _chatList.value.find { it.id == chatId }
    }

    fun clearChatRoom() {
        curChat = null
        messageCursor = null
        _messageList.value = listOf<MessageInfo>()
    }

    fun getChatList() {
        _chatListState.value = SlackState("0", null, null, null)
        viewModelScope.launch {
            val response = wafflyApiService.getChatListPaged(size = 10, cursor = chatListCursor)
            try {
                if (response.isSuccessful) {
                    _chatList.value = chatList.value + response.body()!!.contents
                            _chatListState.value = SlackState("200", null, null, null)
                    if (response.body()!!.size > 0) chatListCursor = response.body()!!.cursor
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _chatListState.value = SlackState(
                        errorResponse.statusCode,
                        errorResponse.errorCode,
                        errorResponse.message,
                        null
                    )
                }
            } catch (e: Exception) {
                _messagesState.value = SlackState("-1", null, "System Corruption", null)
            }
        }
    }

    fun getMessages() {
        _messagesState.value = SlackState("0", null, null, null)
        viewModelScope.launch {
            val response = wafflyApiService.getMessagesPaged(
                chatId = curChat!!.id,
                size = 20,
                cursor = messageCursor
            )
            try {
                if (response.isSuccessful) {
                    _messageList.value = response.body()!!.contents + messageList.value
                    _messagesState.value = SlackState("200", null, null, null)
                    if (response.body()!!.size > 0) messageCursor = response.body()!!.cursor
                    markAsRead(curChat!!.id)
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _messagesState.value = SlackState(
                        errorResponse.statusCode,
                        errorResponse.errorCode,
                        errorResponse.message,
                        null
                    )
                }
            } catch (e: Exception) {
                _messagesState.value = SlackState("-1", null, "System Corruption", null)
            }
        }
    }

    fun markAsRead(chatId: Long) {
        _chatList.value = _chatList.value.map {
            if (it.id == chatId) {
                it.copy(
                    unread = 0
                )
            } else {
                it
            }
        }
    }

    fun startWebSocket() {
        webSocketManager.init("http://api.wafflytime.com/api/ws-connect", this)
        webSocketManager.connect()
    }

    override fun onConnectSuccess() {
        Log.d("CHAT", "onConnectSuccess")
    }

    override fun onConnectFailed() {
        Log.d("CHAT", "onConnectFailed")
    }

    override fun onMessage(text: String) {
        Log.d("onMessage", text)
        val serverMessage = moshi.adapter(WebSocketServerMessage::class.java)
            .fromJson(text)!!
        when (serverMessage.type) {
            "MESSAGE" -> {
                val info = moshi.adapter(WebChatMessageInfo::class.java).fromJson(text)!!
                if(curChat?.id == info.chatId) {
                    _messageList.value = _messageList.value.toList() + MessageInfo(
                        id = info.chatId,
                        sentAt = info.sentAt,
                        received = info.received,
                        contents = info.contents
                    )
                }
                val temp = _chatList.value.find {it.id == info.chatId}!!
                val newList = _chatList.value.toMutableList()
                newList.remove(temp)
                newList.add(
                    0, temp.copy(
                        recentMessage = info.contents,
                        recentTime = info.sentAt,
                        unread = if(curChat?.id != info.chatId) temp.unread + 1 else temp.unread
                    )
                )
                _chatList.value = newList
            }
            "NEWCHAT" -> {
                val info = moshi.adapter(WebChatCreationInfo::class.java).fromJson(text)!!
                _chatList.value = mutableListOf(
                    ChatSimpleInfo(
                        id = info.chatId,
                        target = info.target,
                        recentMessage = null,
                        recentTime = null,
                        unread = -1,
                        blocked = false,
                    )
                ) + _chatList.value
            }
            "NEED_UPDATE" -> {
                val info = moshi.adapter(WebChatNeedUpdateInfo::class.java).fromJson(text)!!
                _chatList.value = _chatList.value.map { old ->
                    val unread = info.unread[_chatList.value.indexOfFirst { new ->
                        new.id == old.id
                    }]
                    old.copy(unread = unread)
                }
            }
        }
    }

    fun sendMessage(content: String) {
        val json = moshi.adapter(WebSocketClientMessage::class.java)
            .toJson(WebSocketClientMessage(curChat!!.id, content))
        webSocketManager.sendMessage(json)
    }

    override fun onClose() {
        val chatIds = _chatList.value.map { it.id }
        val unreads = _chatList.value.map { it.unread }
        viewModelScope.launch {
            try {
                val response = wafflyApiService.putUnread(PutUnreadRequest(chatIds, unreads))
                if (response.isSuccessful) {
                    Log.i("WEBSOCKET", "Put unread success")
                }
                else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    Log.i("WEBSOCKET", "Put unread failed: ${errorResponse.message}")
                }
            } catch (e: Exception) {
                Log.i("WEBSOCKET", "System Corruption")
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("CHATVIEWMODEL", "onCleared()")
        webSocketManager.close()
    }

    fun blockChatRoom(block: Boolean) {
        _blockState.value = SlackState("0", null, null, null)
        viewModelScope.launch {
            val response = wafflyApiService.blockChatRoom(curChat!!.id, BlockChatRoomRequest(block))
            try {
                if(response.isSuccessful) {
                    curChat = response.body()!!
                    _blockState.value = SlackState("200", null, null, null)
                }
                else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _blockState.value = SlackState(
                        errorResponse.statusCode,
                        errorResponse.errorCode,
                        errorResponse.message,
                        null
                    )
                }
            }
            catch (e: Exception) {
                _blockState.value = SlackState("-1", null, "System Corruption", null)
            }
        }
    }
}