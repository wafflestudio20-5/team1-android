package com.waffle22.wafflytime.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.NotificationData
import com.waffle22.wafflytime.util.StateStorage
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class NotifyState(
    val status: String,
    val errorCode: String?,
    val errorMessage: String?,
    val notificationDataSet: List<NotificationData>?
)

class NotifyViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
): ViewModel() {
    private val _notifyState: MutableStateFlow<NotifyState> = MutableStateFlow(NotifyState("0",null,null,null))
    val notifyState: StateFlow<NotifyState> = _notifyState
    private lateinit var notificationDataset: List<NotificationData>

    fun resetNotifyState(){
        _notifyState.value = NotifyState("0",null,null, null)
    }

    fun getNotifications() {
        viewModelScope.launch {
            try {
                val response = wafflyApiService.getNotifications(0, 20)

                if (response.isSuccessful) {
                    notificationDataset = response.body()!!.notifications
                    _notifyState.value = NotifyState("200", null, null, notificationDataset)
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _notifyState.value = NotifyState(errorResponse.statusCode, errorResponse.errorCode, errorResponse.message, null)
                }
            } catch (e:java.lang.Exception) {
                _notifyState.value = NotifyState("-1", null, "system Corruption", null)
            }
        }
    }
}