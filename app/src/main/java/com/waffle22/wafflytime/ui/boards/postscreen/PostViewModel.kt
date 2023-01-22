package com.waffle22.wafflytime.ui.boards.postscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.BoardDTO
import com.waffle22.wafflytime.network.dto.PostResponse
import com.waffle22.wafflytime.network.dto.ReplyRequest
import com.waffle22.wafflytime.network.dto.ReplyResponse
import com.waffle22.wafflytime.util.AuthStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class PostStatus{
    StandBy, Success, NotFound, BadRequest, Corruption
}

class PostViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage
) : ViewModel() {
    private val _postState = MutableStateFlow<PostStatus>(PostStatus.StandBy)
    val postState: StateFlow<PostStatus>
        get() = _postState
    private val _repliesState = MutableStateFlow(PostStatus.StandBy)
    val repliesState: StateFlow<PostStatus>
        get() = _repliesState
    private lateinit var _curBoard : BoardDTO
    val curBoard: BoardDTO
        get() = _curBoard
    private var _curPost = MutableLiveData<PostResponse>()
    val curPost: LiveData<PostResponse>
        get() = _curPost
    private var _replies = MutableLiveData<List<ReplyResponse>>()
    val replies: LiveData<List<ReplyResponse>>
        get() = _replies

    fun refresh(boardId: Long, postId: Long){
        _postState.value = PostStatus.StandBy
        _repliesState.value = PostStatus.StandBy
        getPost(boardId, postId)
        getReplies(boardId, postId)
    }

    fun getPost(boardId: Long, postId: Long){
        viewModelScope.launch {
            try{
                val boardResponse = wafflyApiService.getSingleBoard(boardId)
                when (boardResponse.code().toString()){
                    "200" -> {
                        _curBoard = boardResponse.body()!!
                        val response = wafflyApiService.getSinglePost(boardId, postId)
                        when (response.code().toString()){
                            "200" -> {
                                _curPost.value = response.body()
                                _postState.value = PostStatus.Success
                            }
                            "505" -> _postState.value = PostStatus.NotFound
                            "506" -> _postState.value = PostStatus.BadRequest
                        }
                    }
                    "404" -> _postState.value = PostStatus.NotFound
                }
            } catch (e: java.lang.Exception){
                _postState.value = PostStatus.Corruption
            }
        }
    }

    fun getReplies(boardId: Long, postId: Long){
        viewModelScope.launch {
            try{
                _repliesState.value = PostStatus.StandBy
                val response = wafflyApiService.getReplies(boardId, postId)
                when (response.code().toString()){
                    "200" -> {
                        _replies.value = response.body()!!.content ?: listOf()
                        _repliesState.value = PostStatus.Success
                    }
                    "505" -> _repliesState.value = PostStatus.NotFound
                    "506" -> _repliesState.value = PostStatus.BadRequest
                }
            } catch (e: java.lang.Exception) {
                _repliesState.value = PostStatus.Corruption
            }
        }
    }

    fun createReply(contents: String, parent:Long?, isAnonymous: Boolean){
        if (contents == "") return
        viewModelScope.launch {
            try{
                val replyRequest = ReplyRequest(contents,parent,isAnonymous)
                val response = wafflyApiService.createReply(_curBoard.boardId, _curPost.value!!.postId, replyRequest)
                when (response.code().toString()){
                    "200" -> {
                        getReplies(_curBoard.boardId, _curPost.value!!.postId)
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.v("PostViewModel", e.toString())
            }
        }
    }

    fun deletePost(){
        viewModelScope.launch {
            try {
                val response = wafflyApiService.deletePost(_curBoard.boardId, _curPost.value!!.postId)
                when (response.code().toString()){
                    "200" -> {
                        Log.d("PostViewModel", "Delete Success")
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.v("PostViewModel", e.toString())
            }
        }
    }

    fun canEditPost(): Boolean{
        return true
    }

    fun canEditReply(reply: ReplyResponse): Boolean{
        return true
    }
}