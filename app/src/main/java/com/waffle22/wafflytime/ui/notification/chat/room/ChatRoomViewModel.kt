package com.waffle22.wafflytime.ui.notification.chat.room

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.BlockChatRoomRequest
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


    val messagesPager = Pager(PagingConfig(pageSize = 20)) {
        ChatRoomPagingSource(chatId, wafflyApiService)
    }.flow.cachedIn(viewModelScope)


    private val _blockState = MutableStateFlow(SlackState("0", null, null, null))
    val blockState: StateFlow<SlackState<Nothing>> = _blockState


    fun sendMessage(content: String) {
//        resetMessagesState()
//        viewModelScope.launch {
//            val response = wafflyApiService.sendChatMessage(chatId, SendChatRequest(content))
//            try {
//                if (response.isSuccessful) {
//                    messages = messages + response.body()!!
//                    _messagesState.value = SlackState(
//                        "200",
//                        null,
//                        null,
//                        messages
//                    )
//                }
//                else {
//                    val errorResponse = HttpException(response).parseError(moshi)!!
//                    _messagesState.value = SlackState(
//                        errorResponse.statusCode,
//                        errorResponse.errorCode,
//                        errorResponse.message,
//                        null
//                    )
//                }
//            }
//            catch (e: Exception) {
//                _messagesState.value = SlackState("-1", null, "System Corruption", null)
//            }
//        }
    }

    fun blockChatRoom(block: Boolean) {
        resetBlockState()
        viewModelScope.launch {
            val response = wafflyApiService.blockChatRoom(chatId, BlockChatRoomRequest(block))
            try {
                if(response.isSuccessful) {
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

    fun resetBlockState() {
        _blockState.value = SlackState("0", null, null, null)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("CHATROOM", "onCleared")
    }
}