package com.waffle22.wafflytime.ui.boards.boardscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
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

class BoardViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage,
    private val moshi: Moshi
) : ViewModel() {
    private var _posts = MutableLiveData<MutableList<PostResponse>>()
    val posts: LiveData<MutableList<PostResponse>>
        get() = _posts
    private var _announcements = MutableLiveData<MutableList<PostResponse>>()
    val announcements: LiveData<MutableList<PostResponse>>
        get() = _announcements
    private var _postsLoadingState = MutableStateFlow<PostsLoadingStatus>(PostsLoadingStatus.Standby)
    val postsLoadingState: StateFlow<PostsLoadingStatus>
        get() = _postsLoadingState
    private var _page = 0
    private val PAGE_SIZE = 20;

    init{
        _posts.value = mutableListOf()
        _announcements.value = mutableListOf()
    }

    fun getPosts(boardId: Long, boardType: BoardType){
        viewModelScope.launch {
            try{
                val response = when(boardType){
                    BoardType.Common -> wafflyApiService.getAllPosts(
                        authStorage.authInfo.value!!.accessToken,
                        boardId, _page, PAGE_SIZE
                    )
                    BoardType.MyPosts -> wafflyApiService.getMyPosts(
                        authStorage.authInfo.value!!.accessToken,
                        _page, PAGE_SIZE
                    )
                    BoardType.Scraps -> wafflyApiService.getMyScraps(
                        authStorage.authInfo.value!!.accessToken,
                        _page, PAGE_SIZE
                    )
                    else -> {null}
                }
                when(response!!.code().toString()){
                    "200" -> {
                        _postsLoadingState.value = PostsLoadingStatus.Success
                        for (newPost in response!!.body()!!.contents!!){
                            var alreadyExists = false
                            for (oldPost in _posts.value!!){
                                if (newPost.postId == oldPost.postId){
                                    alreadyExists = true
                                    break
                                }
                            }
                            if (! alreadyExists) {
                                _posts.value!! += newPost
                                if(newPost.isQuestion)
                                    _announcements.value!! += newPost
                            }
                        }
                    }
                    else -> {
                        when(HttpException(response!!).parseError(moshi)!!.errorCode){
                            "103" -> _postsLoadingState.value = PostsLoadingStatus.TokenExpired
                            else -> _postsLoadingState.value = PostsLoadingStatus.Error
                        }
                    }
                }
            } catch (e: java.lang.Exception){
                Log.v("BoardViewModel", e.toString())
            }
        }
    }

    fun refreshToken(){
        viewModelScope.launch {
            //Log.v("BoardViewModel", "try refresh")
            try {
                val response = wafflyApiService.refresh(authStorage.authInfo.value!!.refreshToken)
                when (response.code().toString()) {
                    "200" -> {
                        //Log.v("BoardViewModel", "refresh success")
                        authStorage.setAuthInfo(
                            response.body()!!.accessToken,
                            response.body()!!.refreshToken
                        )
                        _postsLoadingState.value = PostsLoadingStatus.Standby
                    }
                    else -> _postsLoadingState.value = PostsLoadingStatus.Error
                }
            } catch (e:java.lang.Exception) {
                _postsLoadingState.value = PostsLoadingStatus.Corruption
            }
        }
    }
}