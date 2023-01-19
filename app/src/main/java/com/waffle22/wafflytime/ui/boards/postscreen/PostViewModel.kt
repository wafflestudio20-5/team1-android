package com.waffle22.wafflytime.ui.boards.postscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.BoardDTO
import com.waffle22.wafflytime.network.dto.PostResponse
import com.waffle22.wafflytime.network.dto.ReplyResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class PostStatus{
    StandBy, Success, NotFound, BadRequest, Corruption
}

class PostViewModel(
    private val wafflyApiService: WafflyApiService
) : ViewModel() {
    private val _postState = MutableStateFlow<PostStatus>(PostStatus.StandBy)
    val postState: StateFlow<PostStatus>
        get() = _postState
    private val _commentsState = MutableStateFlow(PostStatus.StandBy)
    val commentsState: StateFlow<PostStatus>
        get() = _commentsState
    private lateinit var _curBoard : BoardDTO
    val curBoard: BoardDTO
        get() = _curBoard
    private var _curPost = MutableLiveData<PostResponse>()
    val curPost: LiveData<PostResponse>
        get() = _curPost
    private var _comments = MutableLiveData<List<ReplyResponse>>()
    val comments: LiveData<List<ReplyResponse>>
        get() = _comments

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

    fun getComments(boardId: Long, postId: Long){
        viewModelScope.launch {
            try{
                val response = wafflyApiService.getComments(boardId, postId)
                when (response.code().toString()){
                    "200" -> {
                        _comments.value = response.body()!!.content ?: listOf()
                        _commentsState.value = PostStatus.Success
                    }
                    "505" -> _commentsState.value = PostStatus.NotFound
                    "506" -> _commentsState.value = PostStatus.BadRequest
                }
            } catch (e: java.lang.Exception) {
                _commentsState.value = PostStatus.Corruption
            }
        }
    }
}