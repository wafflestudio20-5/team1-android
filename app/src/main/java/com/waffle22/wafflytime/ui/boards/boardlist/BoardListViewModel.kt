package com.waffle22.wafflytime.ui.boards.boardlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffle22.wafflytime.data.Board
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.BoardAbstract
import com.waffle22.wafflytime.network.dto.BoardDTO
import com.waffle22.wafflytime.network.dto.BoardListResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class BoardLoadingStatus {
    Standby, Success, Corruption
}

data class TaggedBoards(
    val tag: String,
    val entries: MutableList<Board>
)

class BoardListViewModel(
    private val wafflyApiService: WafflyApiService
) : ViewModel() {
    private var _allBoards = MutableLiveData<List<BoardListResponse>>()
    val allBoards: LiveData<List<BoardListResponse>>
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

    /*
    init{
        _allBoards.value = apiService.getAllBoards()
        _defaultBoards.value = _allBoards.value!!.filter{it.default}
        _allBoardsFiltered.value = _allBoards.value!!.filter{!it.default}
        _taggedBoards.value = mutableListOf()
        for (board in _allBoards.value!!){
            for (tag in board.tag) {
                var isInList = false
                for (i in 0 until _taggedBoards.value!!.size){
                    if(_taggedBoards.value!![i].tag == tag){
                        _taggedBoards.value!![i].entries += board
                        isInList = true
                        break
                    }
                }
                if (!isInList){
                    _taggedBoards.value!! += TaggedBoards(tag, mutableListOf(board))
                }
            }
        }
        Log.v("ViewModel", _taggedBoards.value!!.size.toString())
    }
    */

    fun getAllBoards(){
        viewModelScope.launch {
            try{
                val response = wafflyApiService.getAllBoards()
                when (response.code().toString()){
                    "200" -> {
                        _boardLoadingState.value = BoardLoadingStatus.Success
                        _allBoards.value = response.body()
                        _taggedBoards.value = mutableListOf()
                        for (boardListResponse in _allBoards.value!!){
                            when (boardListResponse.category){
                                "BASIC" -> _basicBoards.value = boardListResponse.boards
                                "OTHER" -> _customBoards.value = boardListResponse.boards
                                else -> _taggedBoards.value?.plusAssign(boardListResponse)
                            }
                        }
                    }
                }

            } catch (e: java.lang.Exception){
                _boardLoadingState.value = BoardLoadingStatus.Corruption
            }
        }
    }

}