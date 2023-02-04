package com.waffle22.wafflytime.ui.boards.boardscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.PostResponse
import com.waffle22.wafflytime.util.SlackState
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class PostsPageHolder(
    var postsList: MutableList<PostResponse>?
)

class SearchPostViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
) : ViewModel() {
    private val _searchPostState: MutableSharedFlow<SlackState<PostsPageHolder>> = MutableSharedFlow(replay = 0)
    val searchPostState: SharedFlow<SlackState<PostsPageHolder>> = _searchPostState

    private var currentState: SlackState<PostsPageHolder> =
        SlackState("0",null,null,PostsPageHolder(mutableListOf()))
    private val currentPageNation: PageNation = PageNation(0)

    private var reservedRefresh: Boolean = false

    // OnCreate 시점에서 호출되는 함수
    fun reInitViewModel() {
        viewModelScope.launch {
            _searchPostState.resetReplayCache()
            currentState = SlackState("0",null,null,PostsPageHolder(mutableListOf()))
            // Make StateFlow
            _searchPostState.emit(currentState)
        }
    }

    // OnViewCreated 에서 호출되는 함수.
    // reservedRefresh 가 true 면 새로고침해서 emit 한다.
    fun requestData(boardId: Long?, keyword: String) {
        viewModelScope.launch {
            if (reservedRefresh){
                refreshBoard(boardId, keyword)
                reservedRefresh = false
            }
            _searchPostState.emit(currentState)
        }

    }

    // 아래있는 데이터 요청했을때 호출되는 함수
    fun getBelowBoard(boardId: Long?, keyword: String){
        viewModelScope.launch {
            if (!currentPageNation.isEnd) {
                val resultPosts = getPosts(boardId, keyword)
                currentState.status = resultPosts.statusCode
                currentState.errorCode = resultPosts.statusCode
                currentState.errorMessage = resultPosts.errorMessage
                _searchPostState.emit(currentState)
            }
        }
    }

    fun setRefresh() {
        reservedRefresh = true
    }

    private suspend fun refreshBoard(boardId: Long?, keyword: String){
        currentState =
            SlackState("0",null,null,PostsPageHolder(mutableListOf()))
        currentPageNation.cursor = null
        currentPageNation.isEnd = false
        val resultPosts = getPosts(boardId, keyword)
        // Make CurrentState
        if (resultPosts.done){
            currentState.status = "200"
            currentState.errorCode = null
            currentState.errorMessage = null
        } else{
            val errorResult = resultPosts
            currentState.status = errorResult.statusCode
            currentState.errorCode = errorResult.statusCode
            currentState.errorMessage = errorResult.errorMessage
        }
    }

    private suspend fun getPosts(boardId: Long?, keyword: String): NetWorkResultReturn{
        try{
            val response =
                if (boardId == null) wafflyApiService.globalSearch(keyword, currentPageNation.cursor, currentPageNation.pageSize)
                else wafflyApiService.boardSearch(boardId, keyword, currentPageNation.cursor, currentPageNation.pageSize)

            return if(response.isSuccessful){
                currentPageNation.cursor = response.body()!!.cursor
                if (currentPageNation.isEnd) currentPageNation.isEnd = true
                currentState.dataHolder!!.postsList!!.addAll(response.body()!!.content)
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