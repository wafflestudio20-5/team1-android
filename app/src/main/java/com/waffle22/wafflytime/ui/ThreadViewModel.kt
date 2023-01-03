package com.waffle22.wafflytime.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.waffle22.wafflytime.data.Comment
import com.waffle22.wafflytime.data.Reply
import com.waffle22.wafflytime.data.WaffThread

class ThreadViewModel : ViewModel() {
    private var _waffThread = MutableLiveData<WaffThread>()
    val waffThread: LiveData<WaffThread>
        get() = _waffThread
    private var _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>>
        get() = _comments

    init{
        _waffThread.value = WaffThread(
            1,
            "Nickname",
            "1234",
            "22/12/31 22:00:34",
            "Hello World Hello World",
            100, 10, "",
            50, listOf(
                Comment(
                    3,
                    "Nickname",
                    "124",
                    "22/12/31 22:00:34",
                    "CommentComment", 100,
                    listOf(
                        Reply(
                            4,
                            "Nickname",
                            "1234",
                            "22/12/31 22:00:34",
                            "ReplyReply",
                            100
                        ),
                        Reply(
                            5,
                            "Nickname",
                            "1234",
                            "22/12/31 22:00:34",
                            "This is a reply",
                            100
                        )
                    )
                )
            )
        )
        _comments.value = _waffThread.value!!.comments
    }
}