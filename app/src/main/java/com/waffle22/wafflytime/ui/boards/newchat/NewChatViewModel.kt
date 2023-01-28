package com.waffle22.wafflytime.ui.boards.newchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.NewChatRequest
import com.waffle22.wafflytime.util.StateStorage
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class NewChatViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
): ViewModel() {

    private val _newChatState: MutableStateFlow<StateStorage> = MutableStateFlow(StateStorage("0",null,null))
    val newChatState: StateFlow<StateStorage> = _newChatState

    fun resetState() {
        _newChatState.value = StateStorage("0",null,null)
    }

    fun sendNewChat(boardId: Long, postId: Long, replyId: Long, isAnonymous: Boolean, content: String){
        viewModelScope.launch {
            if (content.isEmpty()) {
                _newChatState.value = StateStorage("-2",null,null)
            } else{
                try{
                    val checkReplyId = if (replyId==-1L) null else replyId
                    val response = wafflyApiService.sendNewChat(boardId, postId, checkReplyId, NewChatRequest(isAnonymous, content))
                    if (response.isSuccessful) {
                        _newChatState.value = StateStorage("200",null,null)
                    } else {
                        val errorResponse = HttpException(response).parseError(moshi)!!
                        _newChatState.value = StateStorage(errorResponse.statusCode,errorResponse.errorCode,errorResponse.message)
                    }
                } catch (e:java.lang.Exception) {
                    _newChatState.value = StateStorage("-1",null,"System Corruption")
                }
            }
        }
    }
}