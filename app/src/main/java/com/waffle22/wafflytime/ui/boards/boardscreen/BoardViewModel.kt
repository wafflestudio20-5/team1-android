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
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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

data class NetWorkResultReturn(
    var done: Boolean, var statusCode: String, var errorCode: String?, var errorMessage: String?
)

class BoardViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
) : ViewModel() {///MutableStateFlow(INITIAL_STATE)
    private val _boardScreenState: MutableSharedFlow<SlackState<BoardDataHolder>> = MutableSharedFlow(replay = 0)
    val boardScreenState: SharedFlow<SlackState<BoardDataHolder>> = _boardScreenState

    private var currentState: SlackState<BoardDataHolder> =
        SlackState("0",null,null,BoardDataHolder(null, mutableListOf()))
    private val currentPageNation: PageNation = PageNation(0)

    private var reservedRefresh: Boolean = false

    // OnCreate 시점에서 호출되는 함수
    fun reInitViewModel(boardId: Long, boardType: BoardType) {
        viewModelScope.launch {
            _boardScreenState.resetReplayCache()
            refreshBoard(boardId, boardType)
            // Make StateFlow
            _boardScreenState.emit(currentState)
        }
    }

    // OnViewCreated 에서 호출되는 함수.
    // reservedRefresh 가 true 면 새로고침해서 emit 한다.
    fun requestData(boardId: Long, boardType: BoardType) {
        viewModelScope.launch {
            if (reservedRefresh){
                refreshBoard(boardId, boardType)
                reservedRefresh = false
            }
            _boardScreenState.emit(currentState)
        }

    }
    
    // 아래있는 데이터 요청했을때 호출되는 함수
    fun getBelowBoard(boardId: Long, boardType: BoardType){
        viewModelScope.launch {
            if (!currentPageNation.isEnd) {
                val resultPosts = getPosts(boardId, boardType)
                currentState.status = resultPosts.statusCode
                currentState.errorCode = resultPosts.statusCode
                currentState.errorMessage = resultPosts.errorMessage
                _boardScreenState.emit(currentState)
            }
        }
    }

    fun setRefresh() {
        reservedRefresh = true
    }

    private suspend fun refreshBoard(boardId: Long, boardType: BoardType){
        currentState =
            SlackState("0",null,null,BoardDataHolder(null, mutableListOf()))
        currentPageNation.cursor = null
        currentPageNation.isEnd = false
        val resultBoardInfo = getBoardInfo(boardId, boardType)
        val resultPosts = getPosts(boardId, boardType)
        // Make CurrentState
        if (resultBoardInfo.done && resultPosts.done){
            currentState.status = "200"
            currentState.errorCode = null
            currentState.errorMessage = null
        } else{
            val errorResult = if(!resultBoardInfo.done) resultBoardInfo else resultPosts
            currentState.status = errorResult.statusCode
            currentState.errorCode = errorResult.statusCode
            currentState.errorMessage = errorResult.errorMessage
        }
    }

    private suspend fun getBoardInfo(boardId: Long, boardType: BoardType): NetWorkResultReturn{
           when(boardType) {
               BoardType.MyPosts -> currentState.dataHolder!!.boardInfo = BoardDTO(-1, "SPECIAL", "내가 작성한 글", "", true)
               BoardType.MyReplies -> currentState.dataHolder!!.boardInfo = BoardDTO(-1, "SPECIAL", "내가 댓글을 단 글", "", true)
               BoardType.Scraps -> currentState.dataHolder!!.boardInfo = BoardDTO(-1, "SPECIAL", "스크랩한 글", "", true)
               BoardType.Hot -> currentState.dataHolder!!.boardInfo = BoardDTO(-1, "SPECIAL", "HOT 게시판", "", true)
               BoardType.Best -> currentState.dataHolder!!.boardInfo = BoardDTO(-1, "SPECIAL", "BEST 게시판", "", true)
               BoardType.Common -> {
                       try {
                           val response = wafflyApiService.getSingleBoard(boardId)
                           if (response.isSuccessful) {
                               currentState.dataHolder!!.boardInfo = response.body()!!
                           } else {
                               val errorResponse = HttpException(response).parseError(moshi)!!
                               currentState.dataHolder!!.boardInfo = null
                               return NetWorkResultReturn(false, errorResponse.statusCode, errorResponse.errorCode, errorResponse.message)
                           }
                       } catch (e: java.lang.Exception) {
                           currentState.dataHolder!!.boardInfo = null
                           return NetWorkResultReturn(false, "-1", null,"SystemCorruption")
                       }
               }
            }
        return NetWorkResultReturn(true,"200",null,null)
    }

    private suspend fun getPosts(boardId: Long, boardType: BoardType): NetWorkResultReturn{
        try{
            val response = when(boardType){
                BoardType.Common -> wafflyApiService.getAllPosts(boardId, currentPageNation.cursor, currentPageNation.pageSize)
                BoardType.MyPosts -> wafflyApiService.getMyPosts(currentPageNation.cursor, currentPageNation.pageSize)
                BoardType.MyReplies -> wafflyApiService.getMyRepliedPost(currentPageNation.cursor, currentPageNation.pageSize)
                BoardType.Scraps -> wafflyApiService.getMyScraps(currentPageNation.cursor, currentPageNation.pageSize)
                BoardType.Hot -> wafflyApiService.getHotPosts(currentPageNation.cursor, currentPageNation.pageSize)
                BoardType.Best -> wafflyApiService.getBestPosts(currentPageNation.cursor, currentPageNation.pageSize) // TODO 더블커서...
                else -> {null}
            }

            return if(response!!.isSuccessful){
                currentPageNation.cursor = response.body()!!.cursor
                if (currentPageNation.isEnd) currentPageNation.isEnd = true
                currentState.dataHolder!!.boardData.addAll(response.body()!!.content)
                NetWorkResultReturn(true,"200",null,null)
            } else{
                val errorResponse = HttpException(response).parseError(moshi)!!
                NetWorkResultReturn(false, errorResponse.statusCode, errorResponse.errorCode, errorResponse.message)
            }

        } catch (e: java.lang.Exception){
            //return NetWorkResultReturn(false, "-1", null,"SystemCorruption")
            Log.v("BoardViewModel", e.toString())
            return NetWorkResultReturn(false, "-1", null,e.toString())
        }
    }
}