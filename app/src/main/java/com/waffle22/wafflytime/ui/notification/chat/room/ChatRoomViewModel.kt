package com.waffle22.wafflytime.ui.notification.chat.room

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.MessageInfo
import com.waffle22.wafflytime.network.dto.SendChatRequest
import com.waffle22.wafflytime.util.SlackState
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ChatRoomViewModel(
    private val chatId: Long,
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
) : ViewModel() {

    private var curPage = 0
    companion object {
        private const val pageSize = 20
    }

    private val _messagesState = MutableStateFlow<SlackState<List<MessageInfo>>>(
        SlackState("0", null, null, null)
    )
    val messagesState: StateFlow<SlackState<List<MessageInfo>>> = _messagesState

    private var messages = listOf<MessageInfo>()

    fun getMessages() {
        resetMessagesState()
        viewModelScope.launch {
            val response = wafflyApiService.getChatMessages(chatId, curPage, pageSize)
            try {
                if (response.isSuccessful) {
                    messages = response.body()!!.content + messages
                    _messagesState.value = SlackState(
                        "200",
                        null,
                        null,
                        messages
                    )
                    if(response.body()!!.content.isNotEmpty()) curPage++
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _messagesState.value = SlackState(
                        errorResponse.statusCode,
                        errorResponse.errorCode,
                        errorResponse.message,
                        null
                    )
                }
            }
            catch (e: Exception) {
                _messagesState.value = SlackState("-1", null, "System Corruption", null)
            }
        }
    }

    fun resetMessagesState() {
        _messagesState.value = SlackState("0", null, null, null)
    }

    fun sendMessage(content: String) {
        resetMessagesState()
        viewModelScope.launch {
            val response = wafflyApiService.sendChatMessage(chatId, SendChatRequest(content))
            try {
                if (response.isSuccessful) {
                    messages = messages + response.body()!!
                    _messagesState.value = SlackState(
                        "200",
                        null,
                        null,
                        messages
                    )
                }
                else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _messagesState.value = SlackState(
                        errorResponse.statusCode,
                        errorResponse.errorCode,
                        errorResponse.message,
                        null
                    )
                }
            }
            catch (e: Exception) {
                _messagesState.value = SlackState("-1", null, "System Corruption", null)
            }
        }
    }
}