package com.waffle22.wafflytime.ui.boards.boardscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.waffle22.wafflytime.data.ThreadPreview

class BoardViewModel : ViewModel() {
    private var _threads = MutableLiveData<List<ThreadPreview>>()
    val threads: LiveData<List<ThreadPreview>>
        get() = _threads
    private var _announcements = MutableLiveData<List<ThreadPreview>>()
    val announcements: LiveData<List<ThreadPreview>>
        get() = _announcements

    init{
        _threads.value = listOf(
            ThreadPreview(
                1,
                "Nickname",
                "1234",
                "22/12/31 22:00:34",
                "Hello World Hello World",
                100, 10, ""
            ),
            ThreadPreview(
                2,
                "Nickname2",
                "11234",
                "22/12/31 22:05:34",
                "Hello World Hello World!!!",
                120, 12, "Announce"
            )
        )
        _announcements.value = _threads.value!!.filter { it.tag != "" }
    }


}