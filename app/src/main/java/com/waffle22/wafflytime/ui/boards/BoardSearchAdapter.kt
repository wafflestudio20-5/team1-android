package com.waffle22.wafflytime.ui.boards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.data.Board
import com.waffle22.wafflytime.databinding.BoardSummaryBinding
import com.waffle22.wafflytime.databinding.SearchBoardResultBinding
import com.waffle22.wafflytime.ui.boards.boardlist.BoardListAdapter

class BoardSearchAdapter(private val clicked: () -> Unit)
    : ListAdapter<Board, BoardSearchAdapter.BoardSearchViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardSearchAdapter.BoardSearchViewHolder {
        return BoardSearchAdapter.BoardSearchViewHolder(
            SearchBoardResultBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: BoardSearchAdapter.BoardSearchViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, clicked)
    }

    class BoardSearchViewHolder(private var binding: SearchBoardResultBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(board: Board, clicked: () -> Unit) {
            binding.apply{
                title.text = board.title
                description.text = board.description
                explanations.setOnClickListener{clicked()}
            }
            if(binding.description.text == "")
                binding.description.visibility = View.GONE
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Board>() {
            override fun areContentsTheSame(
                oldItem: Board,
                newItem: Board
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: Board, newItem: Board): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}