package com.waffle22.wafflytime.ui.boards.boardscreen

import android.util.Log
import androidx.compose.runtime.key
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.BoardDTO
import com.waffle22.wafflytime.network.dto.BoardType
import com.waffle22.wafflytime.network.dto.PostResponse
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

enum class PostsLoadingStatus{
    Standby, Success, Corruption, Error, TokenExpired
}


//TODO: 앱 구동 후 첫 번째로 게시판을 로딩하면 질문글(혹은 게시물 전체)이 보이지 않는 오류가 있음

class BoardViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
) : ViewModel() {
    private var _boardInfo = MutableLiveData<BoardDTO>()
    val boardInfo: LiveData<BoardDTO>
        get() = _boardInfo
    private var _allposts = MutableLiveData<MutableList<PostResponse>>()
    private var _posts = MutableLiveData<MutableList<PostResponse>>()
    val posts: LiveData<MutableList<PostResponse>>
        get() = _posts
    private var _announcements = MutableLiveData<MutableList<PostResponse>>()
    val announcements: LiveData<MutableList<PostResponse>>
        get() = _announcements
    private var _searchResults = MutableLiveData<MutableList<PostResponse>>()
    val searchResults : LiveData<MutableList<PostResponse>>
        get() = _searchResults
    private var _postsLoadingState = MutableStateFlow<PostsLoadingStatus>(PostsLoadingStatus.Standby)
    val postsLoadingState: StateFlow<PostsLoadingStatus>
        get() = _postsLoadingState
    private var _page = 0
    private val PAGE_SIZE = 20
    private var _boardId = -1L

    fun refreshBoard(boardId: Long, boardType: BoardType){
        _postsLoadingState.value = PostsLoadingStatus.Standby
        _page = 0
        getPosts(boardId, boardType)
    }

    fun getBoardInfo(boardId: Long, boardType: BoardType){
        if (boardType != BoardType.Common){
            when(boardType){
                BoardType.MyPosts -> _boardInfo.value = BoardDTO(-1,"SPECIAL", "내가 작성한 글", "", true)
                BoardType.MyReplies -> _boardInfo.value = BoardDTO(-1,"SPECIAL", "내가 댓글을 단 글", "", true)
                BoardType.Scraps -> _boardInfo.value = BoardDTO(-1,"SPECIAL", "스크랩한 글", "", true)
                BoardType.Hot -> _boardInfo.value = BoardDTO(-1,"SPECIAL", "HOT 게시판", "", true)
                BoardType.Best -> _boardInfo.value = BoardDTO(-1,"SPECIAL", "BEST 게시판", "", true)
                else -> null
            }
        }
        else{
            viewModelScope.launch {
                try{
                    val response = wafflyApiService.getSingleBoard(boardId)
                    when(response.code().toString()){
                        "200" -> {
                            Log.v("BoardViewModel", response.body()!!.title)
                            _boardInfo.value = response.body()
                        }
                        else -> {
                            Log.v("BoardViewModel", response.errorBody()!!.string())
                            when(HttpException(response!!).parseError(moshi)!!.errorCode){
                                "103" -> _postsLoadingState.value = PostsLoadingStatus.TokenExpired
                                else -> _postsLoadingState.value = PostsLoadingStatus.Error
                            }
                        }
                    }
                } catch (e: java.lang.Exception){
                    _postsLoadingState.value = PostsLoadingStatus.Corruption
                    Log.v("BoardViewModel", e.toString())
                }
            }
        }
    }

    fun getPosts(boardId: Long, boardType: BoardType){
        viewModelScope.launch {
            try{
                if (_boardId != boardId || boardType != BoardType.Common){
                    _page = 0
                    _boardId = boardId
                }
                val response = when(boardType){
                    BoardType.Common -> wafflyApiService.getAllPosts(_boardId, _page, PAGE_SIZE)
                    BoardType.MyPosts -> wafflyApiService.getMyPosts(_page, PAGE_SIZE)
                    BoardType.Scraps -> wafflyApiService.getMyScraps(_page, PAGE_SIZE)
                    BoardType.Hot -> wafflyApiService.getHotPosts(_page, PAGE_SIZE)
                    BoardType.Best -> wafflyApiService.getBestPosts(_page, PAGE_SIZE)
                    else -> {null}
                }
                if (_page == 0){
                    _posts.value = mutableListOf()
                    _announcements.value = mutableListOf()
                }
                when(response!!.code().toString()){
                    "200" -> {
                        _postsLoadingState.value = PostsLoadingStatus.Success
                        if(response.body()!!.content!!.isNotEmpty()){
                            for (newPost in response.body()!!.content!!){
                                var alreadyExists = false
                                for (oldPost in _posts.value!!){
                                    if (newPost.postId == oldPost.postId){
                                        alreadyExists = true
                                        break
                                    }
                                }
                                if (! alreadyExists) {
                                    //if(newPost.isQuestion)  Log.v("BoardViewModel", newPost.contents)
                                    if(newPost.isQuestion) {
                                        if(_announcements.value!!.size < 2) {
                                            _announcements.value!! += newPost
                                            _posts.value!!.add(0, newPost)
                                        }
                                        _posts.value!! += notQuestion(newPost)
                                    }
                                    else _posts.value!! += newPost
                                }
                            }
                            _page += 1
                        }
                    }
                    else -> {
                        when(HttpException(response).parseError(moshi)!!.errorCode){
                            "103" -> _postsLoadingState.value = PostsLoadingStatus.TokenExpired
                            else -> _postsLoadingState.value = PostsLoadingStatus.Error
                        }
                    }
                }
            } catch (e: java.lang.Exception){
                _postsLoadingState.value = PostsLoadingStatus.Corruption
                Log.v("BoardViewModel", e.toString())
            }
        }
    }

    fun searchPost(keyword: String){
        _searchResults.value = mutableListOf()
        if (keyword == "")  return
        for (post in _posts.value!!){
            if (post.contents.contains(keyword))    _searchResults.value!! += post
            else if(post.title!=null){
                if(post.title.contains(keyword))    _searchResults.value!! += post
            }
        }
    }

    private fun notQuestion(response: PostResponse): PostResponse {
        return PostResponse(
            response.boardId,
            response.boardTitle,
            response.postId,
            response.createdAt,
            response.writerId,
            response.nickname,
            response.isWriterAnonymous,
            false,
            response.title,
            response.contents,
            response.images,
            response.nlikes,
            response.nscraps,
            response.nreplies
        )
    }

    fun reset(){
        _page = 0
    }
}