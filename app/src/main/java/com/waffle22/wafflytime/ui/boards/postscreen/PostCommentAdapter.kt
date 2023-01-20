package com.waffle22.wafflytime.ui.boards.postscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.ThreadCommentBinding
import com.waffle22.wafflytime.network.dto.ReplyResponse

class PostCommentAdapter()
    : ListAdapter<ReplyResponse, PostCommentAdapter.ThreadCommentViewHolder>(DiffCallback){

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
        fun bind(reply: ReplyResponse) {
            /*binding.apply{
                nickname.text = comment.nickname
                time.text = comment.time
                commentText.text = comment.text
                likesText.text = comment.likes.toString()
            }
            val postReplyAdapter = PostReplyAdapter()
            postReplyAdapter.submitList(comment.replies)
            binding.replies.adapter = postReplyAdapter
            binding.replies.layoutManager = LinearLayoutManager(this.context)*/
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ReplyResponse>() {
            override fun areContentsTheSame(
                oldItem: ReplyResponse,
                newItem: ReplyResponse
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: ReplyResponse, newItem: ReplyResponse): Boolean {
                return oldItem.postId == newItem.postId
            }
        }
    }
}