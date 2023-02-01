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

data class PageNation(
    var cursor: Int?,
    var pageSize: Int = 20,
    var isEnd: Boolean = false
)

enum class BoardViewModelState{
    Init, Stanby, FromPost, FromCancelThread, FromSendThread
}

class BoardViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
) : ViewModel() {
    private val _boardScreenState: MutableStateFlow<SlackState<BoardDataHolder>> = MutableStateFlow(INITIAL_STATE)
    val boardScreenState: StateFlow<SlackState<BoardDataHolder>> = _boardScreenState

    private val currentData: BoardDataHolder = BoardDataHolder(null, mutableListOf())
    private val currentPageNation: PageNation = PageNation(0)

    var currentViewModelState: BoardViewModelState = BoardViewModelState.Init

    fun launchViewModel(boardId: Long, boardType: BoardType) {
        when(currentViewModelState){
            BoardViewModelState.Init -> {
                refreshBoard(boardId, boardType)
                currentViewModelState = BoardViewModelState.Stanby
                currentViewModelState
            }
            BoardViewModelState.Stanby -> {
                generateData()
            }
            BoardViewModelState.FromPost -> {
                generateData()
                currentViewModelState = BoardViewModelState.Stanby
            }
            BoardViewModelState.FromCancelThread -> {
                generateData()
                currentViewModelState = BoardViewModelState.Stanby
            }
            BoardViewModelState.FromSendThread -> {
                refreshBoard(boardId, boardType)
                currentViewModelState = BoardViewModelState.Stanby
            }
            else -> null
        }
    }

    fun refreshBoard(boardId: Long, boardType: BoardType){
        viewModelScope.launch {
            currentData.boardInfo = null
            currentData.boardData = mutableListOf()
            currentPageNation.cursor = null
            currentPageNation.isEnd = false
            val asyncGetBoardInfo =
                async{
                    getBoardInfo(boardId, boardType)
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

    fun getBelowBoard(boardId: Long, boardType: BoardType){
        viewModelScope.launch {
            getPosts(boardId, boardType)
            _boardScreenState.value = SlackState("200", null, null, currentData)
        }
    }

    suspend fun getBoardInfo(boardId: Long, boardType: BoardType){
       when(boardType) {
           BoardType.MyPosts -> currentData.boardInfo = BoardDTO(-1, "SPECIAL", "내가 작성한 글", "", true)
           BoardType.MyReplies -> currentData.boardInfo = BoardDTO(-1, "SPECIAL", "내가 댓글을 단 글", "", true)
           BoardType.Scraps -> currentData.boardInfo = BoardDTO(-1, "SPECIAL", "스크랩한 글", "", true)
           BoardType.Hot -> currentData.boardInfo = BoardDTO(-1, "SPECIAL", "HOT 게시판", "", true)
           BoardType.Best -> currentData.boardInfo = BoardDTO(-1, "SPECIAL", "BEST 게시판", "", true)
           BoardType.Common -> {
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
    
    // TODO: 커서 기반 페이지네이션으로 바뀌면 currentPageNation.page -> currentPageNation.cursor 로 바꿀것
    suspend fun getPosts(boardId: Long, boardType: BoardType){
        if (currentPageNation.isEnd) {
            return
        }
        try{
            val response = when(boardType){
                BoardType.Common -> wafflyApiService.getAllPosts(boardId, currentPageNation.cursor, currentPageNation.pageSize)
                BoardType.MyPosts -> wafflyApiService.getMyPosts(currentPageNation.cursor, currentPageNation.pageSize)
                BoardType.Scraps -> wafflyApiService.getMyScraps(currentPageNation.cursor, currentPageNation.pageSize)
                BoardType.Hot -> wafflyApiService.getHotPosts(currentPageNation.cursor, currentPageNation.pageSize)
                BoardType.Best -> wafflyApiService.getBestPosts(currentPageNation.cursor, currentPageNation.pageSize)
                else -> {null}
            }

            if(response!!.isSuccessful){
                currentPageNation.cursor = response.body()!!.cursor
                if (currentPageNation.cursor == 1) currentPageNation.isEnd = true
                currentData.boardData.addAll(response.body()!!.content)
            } else{
                val errorResponse = HttpException(response).parseError(moshi)!!
                _boardScreenState.value = SlackState(errorResponse.statusCode, errorResponse.errorCode, errorResponse.message, currentData)
            }

        } catch (e: java.lang.Exception){
            _boardScreenState.value = SlackState("-1", null, "System Corruption", currentData)
        }
    }

    fun generateData(){
        _boardScreenState.value = SlackState("200",null,null,currentData)
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