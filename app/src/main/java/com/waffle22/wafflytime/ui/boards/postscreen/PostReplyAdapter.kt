package com.waffle22.wafflytime.ui.boards.postscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.ThreadReplyBinding
import com.waffle22.wafflytime.network.dto.ReplyResponse
import com.waffle22.wafflytime.network.dto.TimeDTO
import java.time.LocalDate

class PostReplyAdapter()
    : ListAdapter<ReplyResponse, PostReplyAdapter.ThreadReplyViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreadReplyViewHolder {
        return ThreadReplyViewHolder(
            ThreadReplyBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), parent.context
        )
    }

    override fun onBindViewHolder(holder: ThreadReplyViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ThreadReplyViewHolder(private var binding: ThreadReplyBinding, private var context: Context)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(reply: ReplyResponse) {
            binding.apply{
                nickname.text = reply.nickname
                //time.text = ""
                replyText.text = reply.contents
                //likesText.text = reply.
                if (reply.isRoot)   notRoot.visibility = View.GONE
            }
        }
        private fun timeToText(time: TimeDTO): String{
            var timeText = time.month.toString() + '/' + time.day.toString() + ' ' + time.hour.toString() + ':' + time.minute.toString()
            if (LocalDate.now().year != time.year)
                timeText = time.year.toString() + '/' + timeText
            return timeText
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

            override fun areItemsTheSame(
                oldItem: ReplyResponse,
                newItem: ReplyResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}