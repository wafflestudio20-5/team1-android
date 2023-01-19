package com.waffle22.wafflytime.ui.boards.boardlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.BoardSummaryBinding
import com.waffle22.wafflytime.network.dto.BoardAbstract

class BoardListAdapter(private val onClicked: (BoardAbstract) -> Unit)
    : ListAdapter<BoardAbstract, BoardListAdapter.BoardListViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardListViewHolder {
        return BoardListViewHolder(
            BoardSummaryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BoardListViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, onClicked)
    }

    class BoardListViewHolder(private var binding: BoardSummaryBinding)
        : RecyclerView.ViewHolder(binding.root){
            fun bind(board: BoardAbstract, onClicked: (BoardAbstract) -> Unit) {
                binding.apply{
                    title.text = board.name
                    //description.text = board.description
                    layout.setOnClickListener{ onClicked(board) }
                }
                if(binding.description.text == "")
                    binding.description.visibility = View.GONE
            }
        }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<BoardAbstract>() {
            override fun areContentsTheSame(
                oldItem: BoardAbstract,
                newItem: BoardAbstract
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: BoardAbstract, newItem: BoardAbstract): Boolean {
                return oldItem.boardId == newItem.boardId
            }
        }
    }
}