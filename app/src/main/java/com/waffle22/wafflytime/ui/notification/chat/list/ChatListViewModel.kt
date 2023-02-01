package com.waffle22.wafflytime.ui.notification.chat.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.ChatSimpleInfo
import com.waffle22.wafflytime.util.SlackState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatListViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
): ViewModel() {

    val chatListPager = Pager(PagingConfig(pageSize = 20)) {
        ChatListPagingSource(wafflyApiService)
    }.flow.cachedIn(viewModelScope)


    override fun onCleared() {
        super.onCleared()
        Log.d("CHATLIST", "onCleared")
    }
}