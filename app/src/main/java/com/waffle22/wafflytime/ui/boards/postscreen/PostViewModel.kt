package com.waffle22.wafflytime.ui.boards.postscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffle22.wafflytime.data.Comment
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.BoardDTO
import com.waffle22.wafflytime.network.dto.PostResponse
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
    private lateinit var _curBoard : BoardDTO
    val curBoard: BoardDTO
        get() = _curBoard
    private var _curPost = MutableLiveData<PostResponse>()
    val curPost: LiveData<PostResponse>
        get() = _curPost
    private var _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>>
        get() = _comments
    /*
    init{

    }*/

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
                            "404" -> {
                                when(response.message().toString()){
                                    "Not Found" -> _postState.value = PostStatus.NotFound
                                    "Bad Request" -> _postState.value = PostStatus.BadRequest
                                }
                            }
                        }
                    }
                    "404" -> _postState.value = PostStatus.NotFound
                }
            } catch (e: java.lang.Exception){
                _postState.value = PostStatus.Corruption
            }
        }
    }
}