package com.waffle22.wafflytime.ui.boards.boardlist

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.data.Board
import com.waffle22.wafflytime.databinding.BoardTaggedBinding
import com.waffle22.wafflytime.network.dto.BoardAbstract
import com.waffle22.wafflytime.network.dto.BoardListResponse

class TaggedBoardsAdapter(private val parentFragment: BoardListFragment)
    :ListAdapter<BoardListResponse, TaggedBoardsAdapter.TaggedBoardsViewHolder>(DiffCallback){

    private var expanded = SparseArray<Boolean>()

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
            fun bind(expanded: SparseArray<Boolean>, taggedBoards: BoardListResponse,
                parentFragment: BoardListFragment){
                binding.description.text = taggedBoards.category

                val boardListAdapter = BoardListAdapter{
                    val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(it.boardId)
                    parentFragment.findNavController().navigate(action)
                }
                boardListAdapter.submitList(taggedBoards.boards)
                binding.recyclerview.adapter = boardListAdapter
                binding.recyclerview.layoutManager = LinearLayoutManager(this.context)
                if (expanded[adapterPosition] == null)  expanded.put(adapterPosition,false)
                when (expanded[adapterPosition]){
                    true -> binding.recyclerview.visibility = View.VISIBLE
                    else -> binding.recyclerview.visibility = View.GONE
                }
                binding.arrowButton.setOnClickListener{
                    when(expanded[adapterPosition]){
                        true -> {
                            expanded[adapterPosition] = false
                            binding.recyclerview.visibility = View.GONE
                        }
                        else -> {
                            expanded[adapterPosition] = true
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