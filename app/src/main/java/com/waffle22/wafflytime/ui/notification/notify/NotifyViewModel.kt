package com.waffle22.wafflytime.ui.notification.notify

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.NotificationData
import com.waffle22.wafflytime.network.dto.NotificationInfo
import com.waffle22.wafflytime.network.dto.TimeDTO
import com.waffle22.wafflytime.ui.boards.boardscreen.BoardDataHolder
import com.waffle22.wafflytime.util.SlackState
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

private val INITIAL_STATE = SlackState("0",null,null, mutableListOf<NotificationData>())

data class PageNation(
    var cursor: Int?,
    var size: Int,
    var isEnd: Boolean
)

class NotifyViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
): ViewModel() {
    private val _notifyState: MutableStateFlow<SlackState<MutableList<NotificationData>>> = MutableStateFlow(INITIAL_STATE)
    val notifyState: StateFlow<SlackState<MutableList<NotificationData>>> = _notifyState

    private val notificationDataset = mutableListOf<NotificationData>()
    private val currentPageNation = PageNation(null,20, false)

    init {
        Log.d("debug","I'm notifyViewModel")
    }

    fun resetNotifyState(){
        _notifyState.value = SlackState("0",null,null, null)
    }

    fun initNotifications() {
        currentPageNation.isEnd = false
        currentPageNation.cursor = null
        getNewNotifications()
    }

    fun getNewNotifications() {
        if (currentPageNation.isEnd) {
            Log.d("debug","im end")
            return
        }
        viewModelScope.launch {
            try {
                val response = wafflyApiService.getNotifications(currentPageNation.cursor, currentPageNation.size)

                if (response.isSuccessful) {
                    currentPageNation.cursor = response.body()!!.cursor
                    if (currentPageNation.cursor == null) {
                        currentPageNation.isEnd = true
                    }
                    notificationDataset.addAll(response.body()!!.notifications)
                    _notifyState.value = SlackState("200", null, null, notificationDataset)
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _notifyState.value = SlackState(errorResponse.statusCode, errorResponse.errorCode, errorResponse.message, null)
                }
            } catch (e:java.lang.Exception) {
                _notifyState.value = SlackState("-1", null, "system Corruption", null)
            }
        }
    }

    fun getPastNotifications() {
        /*
        viewModelScope.launch {
            try {
                val response = wafflyApiService.getNotifications(page, Size)

                if (response.isSuccessful) {
                    page += 1
                    if(_notificationDataset.size > 1 && _notificationDataset.last() == null){
                        _notificationDataset.removeLast()
                    }
                    _notificationDataset.addAll(response.body()!!.notifications)
                    if (!response.body()!!.last) {
                        _notificationDataset.add(getLoadingNotificationData())
                    }
                    _notifyState.value = NotifyState("200", null, null, notificationDataset)
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _notifyState.value = NotifyState(errorResponse.statusCode, errorResponse.errorCode, errorResponse.message, null)
                }
            } catch (e:java.lang.Exception) {
                _notifyState.value = NotifyState("-1", null, "system Corruption", null)
            }
        }
         */
    }

    fun getLoadingNotificationData(): NotificationData {
        val timeDTO = TimeDTO(-1,-1,-1,-1,-1)
        val info = NotificationInfo(-1,"null",-1,null)
        return NotificationData(-1,"null","null",false,timeDTO,info)
    }

    companion object  {
        const val Size: Int = 10
    }
}