package com.waffle22.wafflytime.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.NotificationData
import com.waffle22.wafflytime.network.dto.NotificationInfo
import com.waffle22.wafflytime.network.dto.TimeDTO
import com.waffle22.wafflytime.util.SlackState
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException


class NotifyViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
): ViewModel() {
    private val _notifyState: MutableStateFlow<SlackState<List<NotificationData>>> = MutableStateFlow(SlackState("0",null,null,null))
    val notifyState: StateFlow<SlackState<List<NotificationData>>> = _notifyState
    /*
    private var page: Int = 0
    private val _notificationDataset: MutableList<NotificationData> = mutableListOf()
    val notificationDataset: List<NotificationData> = _notificationDataset
    */
    private lateinit var _notificationDataset: List<NotificationData>


    fun resetNotifyState(){
        _notifyState.value = SlackState("0",null,null, null)
    }

    fun getNewNotifications() {
        viewModelScope.launch {
            try {
                val response = wafflyApiService.getNotifications(0, Size)

                if (response.isSuccessful) {
                    // 어떤 프로토콜이 필요할 것 같음. 아니면 그냥 아래로는 못내려가게 하던가
                    _notificationDataset = (response.body()!!.notifications)
                    _notifyState.value = SlackState("200", null, null, _notificationDataset)
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