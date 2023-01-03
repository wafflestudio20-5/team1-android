package com.waffle22.wafflytime.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.data.Reply
import com.waffle22.wafflytime.databinding.ThreadReplyBinding

class ThreadReplyAdapter()
    : ListAdapter<Reply, ThreadReplyAdapter.ThreadReplyViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreadReplyViewHolder {
        return ThreadReplyViewHolder(
            ThreadReplyBinding.inflate(
                LayoutInflater.from(parent.context)
            ), parent.context
        )
    }

    override fun onBindViewHolder(holder: ThreadReplyViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ThreadReplyViewHolder(private var binding: ThreadReplyBinding, private var context: Context)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(reply: Reply) {
            binding.apply{
                nickname.text = reply.nickname
                time.text = reply.time
                replyText.text = reply.text
                likesText.text = reply.likes.toString()
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Reply>() {
            override fun areContentsTheSame(
                oldItem: Reply,
                newItem: Reply
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: Reply, newItem: Reply): Boolean {
                return oldItem.postId == newItem.postId
            }
        }
    }
}