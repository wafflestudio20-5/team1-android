package com.waffle22.wafflytime.ui.boards.boardlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waffle22.wafflytime.data.Board
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.BoardDTO
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
    private var _allBoards = MutableLiveData<List<BoardDTO>>()
    val allBoards: LiveData<List<BoardDTO>>
        get() = _allBoards
    private var _defaultBoards = MutableLiveData<List<BoardDTO>>()
    val defaultBoards: LiveData<List<BoardDTO>>
        get() = _defaultBoards
    private var _allBoardsFiltered = MutableLiveData<List<BoardDTO>>()
    val allBoardsFiltered: LiveData<List<BoardDTO>>
        get() = _allBoardsFiltered
    /*
    private var _taggedBoards = MutableLiveData<MutableList<TaggedBoards>>()
    val taggedBoards: LiveData<MutableList<TaggedBoards>>
        get() = _taggedBoards*/
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
                    "" -> {
                        _allBoards.value = response.body()

                    }
                }

            } catch (e: java.lang.Exception){
                _boardLoadingState.value = BoardLoadingStatus.Corruption
            }
        }
    }

}