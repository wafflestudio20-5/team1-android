package com.waffle22.wafflytime.ui.boards.boardlist

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.BoardTaggedBinding
import com.waffle22.wafflytime.network.dto.BoardListResponse
import com.waffle22.wafflytime.network.dto.BoardType

class TaggedBoardsAdapter(private val parentFragment: BoardListFragment)
    :ListAdapter<BoardListResponse, TaggedBoardsAdapter.TaggedBoardsViewHolder>(DiffCallback){

    private var expanded = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaggedBoardsViewHolder {
        return TaggedBoardsViewHolder(
            BoardTaggedBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), parent.context
        )
    }

    override fun onBindViewHolder(holder: TaggedBoardsViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(expanded,current, parentFragment)
    }

    class TaggedBoardsViewHolder(private var binding: BoardTaggedBinding, private var context: Context)
        : RecyclerView.ViewHolder(binding.root){
            fun bind(expanded: SparseBooleanArray, taggedBoards: BoardListResponse,
                parentFragment: BoardListFragment){
                binding.description.text = taggedBoards.category

                val boardListAdapter = BoardListAdapter{
                    val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(it.boardId, BoardType.Common)
                    parentFragment.findNavController().navigate(action)
                }
                boardListAdapter.submitList(taggedBoards.boards)
                binding.recyclerview.adapter = boardListAdapter
                binding.recyclerview.layoutManager = LinearLayoutManager(this.context)
                expanded.put(adapterPosition,false)
                when (expanded[adapterPosition]){
                    true -> binding.recyclerview.visibility = View.VISIBLE
                    else -> binding.recyclerview.visibility = View.GONE
                }
                binding.arrowButton.setOnClickListener{
                    when(expanded[adapterPosition]){
                        true -> {
                            expanded.put(adapterPosition,false)
                            binding.recyclerview.visibility = View.GONE
                        }
                        else -> {
                            expanded.put(adapterPosition, true)
                            binding.recyclerview.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<BoardListResponse>() {
            override fun areContentsTheSame(
                oldItem: BoardListResponse,
                newItem: BoardListResponse
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(
                oldItem: BoardListResponse,
                newItem: BoardListResponse): Boolean {
                return oldItem.category == newItem.category
            }
        }
    }
}