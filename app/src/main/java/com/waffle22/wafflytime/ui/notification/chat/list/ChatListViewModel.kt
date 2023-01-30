package com.waffle22.wafflytime.ui.notification.chat.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.ChatSimpleInfo
import com.waffle22.wafflytime.util.SlackState
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ChatListViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
): ViewModel() {

    private val _chatBoxState: MutableStateFlow<SlackState<List<ChatSimpleInfo>>> = MutableStateFlow(SlackState("0",null,null,null))
    val chatBoxState: StateFlow<SlackState<List<ChatSimpleInfo>>> = _chatBoxState

    private lateinit var chatList: List<ChatSimpleInfo>

    fun resetState() {
        _chatBoxState.value = SlackState("0",null,null,null)
    }

    fun getChatList() {
        viewModelScope.launch{
            try {
                val response = wafflyApiService.getChatList()
                if (response.isSuccessful) {
                    chatList = response.body()!!
                    _chatBoxState.value = SlackState("200", null, null, chatList)
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _chatBoxState.value = SlackState(errorResponse.statusCode, errorResponse.errorCode, errorResponse.message, null)
                }
            } catch (e:java.lang.Exception) {
                _chatBoxState.value = SlackState("-1", null, "SystemCorruption", null)
            }
        }
    }
}