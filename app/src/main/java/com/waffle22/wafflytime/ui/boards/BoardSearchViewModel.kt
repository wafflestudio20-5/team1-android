package com.waffle22.wafflytime.ui.boards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.waffle22.wafflytime.data.Board

class BoardSearchViewModel : ViewModel() {
    private var _searchResults = MutableLiveData<List<Board>>()
    val searchResults: LiveData<List<Board>>
        get() = _searchResults

    init{
        _searchResults.value = listOf(
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
    }
}