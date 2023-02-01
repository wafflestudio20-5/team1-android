package com.waffle22.wafflytime.ui.notification

import androidx.lifecycle.ViewModel
import com.waffle22.wafflytime.network.dto.ChatSimpleInfo
import com.waffle22.wafflytime.network.dto.NotificationData
import kotlinx.coroutines.flow.MutableStateFlow

data class BaseNotificationState(
    val navigationState: NavigationState,
    val dataHolder: Any?
)

enum class NavigationState{
    StanBy, ChatRoom, Post
}

class BaseNotificationViewModel: ViewModel() {
    private val _navigationState: MutableStateFlow<BaseNotificationState> = MutableStateFlow(BaseNotificationState(NavigationState.StanBy, null))
    val navigationState: MutableStateFlow<BaseNotificationState> = _navigationState

    fun resetState(){
        _navigationState.value = BaseNotificationState(NavigationState.StanBy, null)
    }

    fun setStatePost(notificationData: NotificationData) {
        _navigationState.value = BaseNotificationState(NavigationState.Post, notificationData)
    }

    fun setStateChatRoom(chatSimpleInfo: ChatSimpleInfo) {
        _navigationState.value = BaseNotificationState(NavigationState.ChatRoom, chatSimpleInfo)
    }
}