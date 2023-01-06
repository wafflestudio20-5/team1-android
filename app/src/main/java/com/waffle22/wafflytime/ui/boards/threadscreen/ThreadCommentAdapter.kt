package com.waffle22.wafflytime.ui.boards.threadscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.data.Comment
import com.waffle22.wafflytime.databinding.ThreadCommentBinding

class ThreadCommentAdapter()
    : ListAdapter<Comment, ThreadCommentAdapter.ThreadCommentViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreadCommentViewHolder {
        return ThreadCommentViewHolder(
            ThreadCommentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), parent.context
        )
    }

    override fun onBindViewHolder(holder: ThreadCommentViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ThreadCommentViewHolder(private var binding: ThreadCommentBinding, private var context: Context)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.apply{
                nickname.text = comment.nickname
                time.text = comment.time
                commentText.text = comment.text
                likesText.text = comment.likes.toString()
            }
            val threadReplyAdapter = ThreadReplyAdapter()
            threadReplyAdapter.submitList(comment.replies)
            binding.replies.adapter = threadReplyAdapter
            binding.replies.layoutManager = LinearLayoutManager(this.context)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Comment>() {
            override fun areContentsTheSame(
                oldItem: Comment,
                newItem: Comment
            ): Boolean {
                return oldItem == newItem && oldItem.replies == newItem.replies
            }

            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem.postId == newItem.postId
            }
        }
    }
}