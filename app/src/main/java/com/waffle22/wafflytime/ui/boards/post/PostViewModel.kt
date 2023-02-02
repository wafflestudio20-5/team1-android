package com.waffle22.wafflytime.ui.boards.post

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class PostStatus{
    StandBy, Success, NotFound, BadRequest, Corruption
}

data class PageNation(
    var firstCursor: Int?,
    var secondCursor: Int?,
    var pageSize: Int = 20,
    var isEnd: Boolean = false
)

class PostViewModel(
    private val wafflyApiService: WafflyApiService,
) : ViewModel() {
    private val _postState = MutableStateFlow(PostStatus.StandBy)
    val postState: StateFlow<PostStatus>
        get() = _postState
    private val _repliesState = MutableStateFlow(PostStatus.StandBy)
    val repliesState: StateFlow<PostStatus>
        get() = _repliesState
    private val _modifyReplyState = MutableStateFlow(LoadingStatus.Standby)
    val modifyReplyState: StateFlow<LoadingStatus>
        get() = _modifyReplyState

    private lateinit var _curBoard : BoardDTO
    val curBoard: BoardDTO
        get() = _curBoard
    private var _curPost = MutableLiveData<PostResponse>()
    val curPost: LiveData<PostResponse>
        get() = _curPost
    private var _images = MutableLiveData<List<ImageResponse>>()
    val images: LiveData<List<ImageResponse>>
        get() = _images
    private var _replies = MutableLiveData<List<ReplyResponse>>()
    val replies: LiveData<List<ReplyResponse>>
        get() = _replies

    private var isMyPost: Boolean = false

    private val currentPageNation = PageNation(null, null)

    fun refresh(boardId: Long, postId: Long){
        currentPageNation.firstCursor = null
        currentPageNation.secondCursor = null
        currentPageNation.isEnd = false
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
                                _images.value = _curPost.value!!.images ?: listOf()
                                _postState.value = PostStatus.Success
                                isMyPost = response.body()!!.isMyPost
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
        if (currentPageNation.isEnd) return
        viewModelScope.launch {
            try{
                _repliesState.value = PostStatus.StandBy
                val response = wafflyApiService.getReplies(
                    boardId, postId, null, currentPageNation.firstCursor, currentPageNation.secondCursor, currentPageNation.pageSize
                )
                when (response.code().toString()){
                    "200" -> {
                        currentPageNation.firstCursor = response.body()!!.cursor?.first
                        currentPageNation.secondCursor = response.body()!!.cursor?.second
                        currentPageNation.isEnd = response.body()!!.isLast
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

    fun editReply(reply: ReplyResponse, contents: String){
        _modifyReplyState.value = LoadingStatus.Standby
        if (contents == "") return
        viewModelScope.launch {
            try {
                val response = wafflyApiService.editReply(_curBoard.boardId, _curPost.value!!.postId, reply.replyId, EditReplyRequest(contents))
                when (response.code().toString()){
                    "200" -> {
                        Log.d("PostViewModel", "Edit Success")
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.v("PostViewModel", e.toString())
            }
        }
    }

    fun deleteReply(reply: ReplyResponse){
        _modifyReplyState.value = LoadingStatus.Standby
        viewModelScope.launch {
            try {
                val response = wafflyApiService.deleteReply(_curBoard.boardId, _curPost.value!!.postId, reply.replyId)
                if (response.body() == null)
                    Log.d("PostViewModel", "Delete Success")
            } catch (e: java.lang.Exception) {
                Log.v("PostViewModel", e.toString())
            }
        }
    }

    fun canEditPost(): Boolean{
        return isMyPost
    }

    fun canEditReply(): Boolean{
        return false
    }

    fun likePost(){
        viewModelScope.launch {
            try {
                val response = wafflyApiService.likePost(_curBoard.boardId, _curPost.value!!.postId)
                when(response.code().toString()){
                    "200" -> Log.d("PostViewModel", "Like post success")
                    else -> Log.d("PostViewModel", response.errorBody()!!.string())
                }
            } catch (e: java.lang.Exception) {
                Log.v("PostViewModel", e.toString())
            }
        }
    }

   fun scrapPost(){
        viewModelScope.launch {
            try {
                val response = wafflyApiService.scrapPost(_curBoard.boardId, _curPost.value!!.postId)
                when(response.code().toString()){
                    "200" -> Log.d("PostViewModel", "Scrap success")
                    else -> Log.d("PostViewModel", response.errorBody()!!.string())
                }
            } catch (e: java.lang.Exception) {
                Log.v("PostViewModel", e.toString())
            }
        }
    }
}