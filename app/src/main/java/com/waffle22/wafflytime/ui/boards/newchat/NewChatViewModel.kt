package com.waffle22.wafflytime.ui.boards.newchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.NewChatRequest
import kotlinx.coroutines.launch

class NewChatViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
): ViewModel() {

    fun sendNewChat(boardId: Long, postId: Long, isAnonyMous: Boolean, content: String){
        viewModelScope.launch {
            wafflyApiService.sendNewChat(boardId, postId, null, NewChatRequest(isAnonyMous, content))
        }
    }
}