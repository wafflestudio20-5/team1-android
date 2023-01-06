package com.waffle22.wafflytime.ui.boards.boardlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.waffle22.wafflytime.data.Board

data class TaggedBoards(
    val tag: String,
    val entries: MutableList<Board>
)

class BoardListViewModel : ViewModel() {
    private var _allBoards = MutableLiveData<List<Board>>()
    val allBoards: LiveData<List<Board>>
        get() = _allBoards
    private var _defaultBoards = MutableLiveData<List<Board>>()
    val defaultBoards: LiveData<List<Board>>
        get() = _defaultBoards
    private var _allBoardsFiltered = MutableLiveData<List<Board>>()
    val allBoardsFiltered: LiveData<List<Board>>
        get() = _allBoardsFiltered
    private var _taggedBoards = MutableLiveData<MutableList<TaggedBoards>>()
    val taggedBoards: LiveData<MutableList<TaggedBoards>>
        get() = _taggedBoards

    init{
        _allBoards.value = listOf(
            Board(
                1,
                "자유게시판",
                "",
                listOf(),
                true, true
            ),
            Board(
                2,
                "새내기게시판",
                "",
                listOf(),
                true, false
            ),
            Board(
                3,
                "홍보게시판",
                "이것저것 홍보해요",
                listOf("홍보"),
                false, true
            ),
            Board(
                4,
                "야매요리 게시판",
                "냠냠",
                listOf("음식"),
                false, true
            )
        )
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
}