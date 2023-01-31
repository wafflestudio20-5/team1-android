package com.waffle22.wafflytime.ui.boards.boardscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.BoardDTO
import com.waffle22.wafflytime.network.dto.BoardType
import com.waffle22.wafflytime.network.dto.LoadingStatus
import com.waffle22.wafflytime.network.dto.PostResponse
import com.waffle22.wafflytime.util.SlackState
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

private val INITIAL_STATE = SlackState("0",null,null,BoardDataHolder(null, mutableListOf()))

data class BoardDataHolder(
    var boardInfo: BoardDTO?,
    var boardData: MutableList<PostResponse>
)

// TODO: 커서 기반 페이지네이션으로 바뀌면 currentPageNation.page -> currentPageNation.cursor 로 바꿀것
data class PageNation(
    var page: Int,
    var pageSize: Int = 20
)

class BoardViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
) : ViewModel() {
    private val _boardScreenState: MutableStateFlow<SlackState<BoardDataHolder>> = MutableStateFlow(INITIAL_STATE)
    val boardScreenState: StateFlow<SlackState<BoardDataHolder>> = _boardScreenState

    private val currentData: BoardDataHolder = BoardDataHolder(null, mutableListOf())
    private val currentPageNation: PageNation = PageNation(0)

    /*
    private var _boardInfo = MutableLiveData<BoardDTO>()
    val boardInfo: LiveData<BoardDTO>
        get() = _boardInfo
    private var _posts = MutableLiveData<MutableList<PostResponse>>()
    val posts: LiveData<MutableList<PostResponse>>
        get() = _posts
    private var _announcements = MutableLiveData<MutableList<PostResponse>>()
    private var _searchResults = MutableLiveData<MutableList<PostResponse>>()
    val searchResults : LiveData<MutableList<PostResponse>>
        get() = _searchResults
    private var _postsLoadingState = MutableStateFlow(LoadingStatus.Standby)
    val postsLoadingState: StateFlow<LoadingStatus>
        get() = _postsLoadingState
    private var _page = 0
    private val _pageSize = 20
    private var _boardId = -1L
    var errorMessage = ""
     */

    fun refreshBoard(boardId: Long, boardType: BoardType){
        viewModelScope.launch {
            currentData.boardInfo = null
            currentData.boardData = mutableListOf()
            currentPageNation.page = 0
            val asyncGetBoardInfo =
                async{
                    if (boardType == BoardType.Common){
                        getBoardInfo(boardId, boardType)
                    }
                }
            val asyncGetPosts =
                async{
                    getPosts(boardId, boardType)
                }

            asyncGetBoardInfo.await()
            asyncGetPosts.await()

            // Make StateFlow
            _boardScreenState.value = SlackState("200", null, null, currentData)
        }
    }

    fun getBoardInfo(boardId: Long, boardType: BoardType){
       when(boardType) {
           BoardType.MyPosts -> currentData.boardInfo = BoardDTO(-1, "SPECIAL", "내가 작성한 글", "", true)
           BoardType.MyReplies -> currentData.boardInfo = BoardDTO(-1, "SPECIAL", "내가 댓글을 단 글", "", true)
           BoardType.Scraps -> currentData.boardInfo = BoardDTO(-1, "SPECIAL", "스크랩한 글", "", true)
           BoardType.Hot -> currentData.boardInfo = BoardDTO(-1, "SPECIAL", "HOT 게시판", "", true)
           BoardType.Best -> currentData.boardInfo = BoardDTO(-1, "SPECIAL", "BEST 게시판", "", true)
           BoardType.Common -> {
               viewModelScope.launch {
                   try {
                       val response = wafflyApiService.getSingleBoard(boardId)
                       if (response.isSuccessful) {
                           currentData.boardInfo = response.body()!!
                       } else {
                           val errorResponse = HttpException(response).parseError(moshi)!!
                           _boardScreenState.value = SlackState(errorResponse.statusCode, errorResponse.errorCode, errorResponse.message, currentData)
                       }
                   } catch (e: java.lang.Exception) {
                       _boardScreenState.value = SlackState("-1", null, "System Corruption", currentData)
                   }
               }
           }
       }
    }
    
    // TODO: 커서 기반 페이지네이션으로 바뀌면 currentPageNation.page -> currentPageNation.cursor 로 바꿀것
    fun getPosts(boardId: Long, boardType: BoardType){
        viewModelScope.launch {
            try{
                val response = when(boardType){
                    BoardType.Common -> wafflyApiService.getAllPosts(boardId, currentPageNation.page, currentPageNation.pageSize)
                    BoardType.MyPosts -> wafflyApiService.getMyPosts(currentPageNation.page, currentPageNation.pageSize)
                    BoardType.Scraps -> wafflyApiService.getMyScraps(currentPageNation.page, currentPageNation.pageSize)
                    BoardType.Hot -> wafflyApiService.getHotPosts(currentPageNation.page, currentPageNation.pageSize)
                    BoardType.Best -> wafflyApiService.getBestPosts(currentPageNation.page, currentPageNation.pageSize)
                    else -> {null}
                }

                if(response!!.isSuccessful){
                    currentPageNation.page += 1
                    currentData.boardData.addAll(response.body()!!.content)
                } else{
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _boardScreenState.value = SlackState(errorResponse.statusCode, errorResponse.errorCode, errorResponse.message, currentData)
                }

            } catch (e: java.lang.Exception){
                _boardScreenState.value = SlackState("-1", null, "System Corruption", currentData)
            }
        }
    }

    fun resetState(){
        _boardScreenState.value = SlackState("0",null,null,currentData)
    }

    /*
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
            response.isMyPost,
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
     */
}