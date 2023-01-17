package com.waffle22.wafflytime.ui.boards.boardlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.BoardAbstract
import com.waffle22.wafflytime.network.dto.BoardListResponse
import com.waffle22.wafflytime.ui.login.LoginStatus
import com.waffle22.wafflytime.util.AuthStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.waffle22.wafflytime.util.parseError

enum class BoardLoadingStatus {
    Standby, Success, Corruption, TokenExpired, Error
}

class BoardListViewModel(
    private val wafflyApiService: WafflyApiService,
    private val authStorage: AuthStorage
) : ViewModel() {
    private var _allBoards = MutableLiveData<MutableList<BoardAbstract>>()
    val allBoards: LiveData<MutableList<BoardAbstract>>
        get() = _allBoards
    private var _basicBoards = MutableLiveData<List<BoardAbstract>>()
    val basicBoards: LiveData<List<BoardAbstract>>
        get() = _basicBoards
    private var _customBoards = MutableLiveData<List<BoardAbstract>>()
    val customBoards: LiveData<List<BoardAbstract>>
        get() = _customBoards
    private var _taggedBoards = MutableLiveData<MutableList<BoardListResponse>>()
    val taggedBoards: LiveData<MutableList<BoardListResponse>>
        get() = _taggedBoards
    private val _boardLoadingState = MutableStateFlow<BoardLoadingStatus>(BoardLoadingStatus.Standby)
    val boardLoadingState: StateFlow<BoardLoadingStatus>
        get() = _boardLoadingState

    private var _searchResults = MutableLiveData<MutableList<BoardAbstract>>()
    val searchResults: LiveData<MutableList<BoardAbstract>>
        get() = _searchResults

    fun getAllBoards(){
        viewModelScope.launch {
            try{
                Log.v("BoardListViewModel", "getAllBoards()")
                val response = wafflyApiService.getAllBoards(authStorage.authInfo.value!!.accessToken)
                Log.v("BoardListViewModel", response.toString())
                when(response.code().toString()) {
                    "200" -> {
                        _boardLoadingState.value = BoardLoadingStatus.Success
                        _allBoards . value = mutableListOf ()
                        _taggedBoards . value = mutableListOf ()
                        for (boardListResponse in response.body()!!) {
                            for (board in boardListResponse.boards!!)
                                _allBoards.value?.plusAssign(board)
                            when (boardListResponse.category) {
                                "BASIC" -> _basicBoards.value = boardListResponse.boards!!
                                "OTHER" -> _customBoards.value = boardListResponse.boards!!
                                else -> _taggedBoards.value?.plusAssign(boardListResponse)
                            }
                        }
                    }
                    else -> {
                        _boardLoadingState.value = BoardLoadingStatus.Error
                    }
                    /*"401" -> {
                        _boardLoadingState.value = BoardLoadingStatus.TokenExpired
                    }
                    else -> {
                        _boardLoadingState.value = BoardLoadingStatus.Error
                        Log.v("BoardListViewModel", "request failed: "+response.errorBody()?.string())
                    }*/
                }
            } catch (e: java.lang.Exception){
                _boardLoadingState.value = BoardLoadingStatus.Corruption
                Log.v("BoardListViewModel", e.toString())
            }
        }
    }

    fun searchBoard(keyword: String){
        _searchResults.value = mutableListOf()
        if (keyword == "")  return
        for (board in _allBoards.value!!){
            if (board.name.contains(keyword))
                _searchResults.value?.plusAssign(board)
        }
    }

    fun refresh(){
        viewModelScope.launch {
            Log.v("BoardListViewModel", "try refresh")
            try {
                val response = wafflyApiService.refresh(authStorage.authInfo.value!!.refreshToken)
                when (response.code().toString()) {
                    "200" -> {
                        Log.v("BoardListViewModel", "refresh success")
                        authStorage.setAuthInfo(
                            response.body()!!.accessToken,
                            response.body()!!.refreshToken
                        )
                        _boardLoadingState.value = BoardLoadingStatus.Standby
                    }
                }
            } catch (e:java.lang.Exception) {
                _boardLoadingState.value = BoardLoadingStatus.Corruption
            }
        }
    }
}