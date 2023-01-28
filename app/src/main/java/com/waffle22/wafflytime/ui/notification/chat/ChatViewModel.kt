package com.waffle22.wafflytime.ui.notification.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import kotlinx.coroutines.launch

class ChatViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
): ViewModel() {
    /*
    fun sendMessage() {
        viewModelScope.launch {
            wafflyApiService.firstChat()
        }
    }
*/
}