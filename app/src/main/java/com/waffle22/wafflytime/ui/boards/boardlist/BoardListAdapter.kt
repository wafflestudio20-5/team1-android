package com.waffle22.wafflytime.ui.boards.boardlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.data.Board
import com.waffle22.wafflytime.databinding.BoardSummaryBinding

class BoardListAdapter()
    : ListAdapter<Board, BoardListAdapter.BoardListViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardListViewHolder {
        return BoardListViewHolder(
            BoardSummaryBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: BoardListViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class BoardListViewHolder(private var binding: BoardSummaryBinding)
        : RecyclerView.ViewHolder(binding.root){
            fun bind(board: Board) {
                binding.apply{
                    title.text = board.title
                    description.text = board.description
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