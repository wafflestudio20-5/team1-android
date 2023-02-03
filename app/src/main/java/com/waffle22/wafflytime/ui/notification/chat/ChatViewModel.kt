package com.waffle22.wafflytime.ui.notification.chat

import androidx.lifecycle.ViewModel
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService

class ChatViewModel(
    private val chatId: Long,
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
): ViewModel() {

}