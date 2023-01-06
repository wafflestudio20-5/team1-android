package com.waffle22.wafflytime.ui.boards.boardscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.data.ThreadPreview
import com.waffle22.wafflytime.databinding.BoardThreadBinding

class ThreadPreviewAdapter(private val clicked: () -> Unit)
    : ListAdapter<ThreadPreview, ThreadPreviewAdapter.ThreadPreviewViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreadPreviewViewHolder {
        return ThreadPreviewViewHolder(
            BoardThreadBinding.inflate(
                LayoutInflater.from(parent.context)
            ), parent.context
        )
    }

    override fun onBindViewHolder(holder: ThreadPreviewViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, clicked)
    }

    class ThreadPreviewViewHolder(private var binding: BoardThreadBinding, private var context: Context)
        : RecyclerView.ViewHolder(binding.root) {
            fun bind(threadPreview: ThreadPreview, clicked: () -> Unit) {
                binding.apply{
                    nickname.text = threadPreview.nickname
                    time.text = threadPreview.time
                    previewText.text = threadPreview.text
                    likesText.text = threadPreview.likes.toString()
                    commentsText.text = threadPreview.comment_cnt.toString()
                    layout.setOnClickListener{clicked()}
                }
            }
        }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ThreadPreview>() {
            override fun areContentsTheSame(
                oldItem: ThreadPreview,
                newItem: ThreadPreview
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: ThreadPreview, newItem: ThreadPreview): Boolean {
                return oldItem.postId == newItem.postId
            }
        }
    }
}