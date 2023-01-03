package com.waffle22.wafflytime.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.data.ThreadPreview
import com.waffle22.wafflytime.databinding.ForumAnnouncementBinding

class ForumAnnouncementAdapter()
    : ListAdapter<ThreadPreview, ForumAnnouncementAdapter.ForumAnnouncementViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumAnnouncementViewHolder {
        return ForumAnnouncementViewHolder(
            ForumAnnouncementBinding.inflate(
                LayoutInflater.from(parent.context)
            ), parent.context
        )
    }

    override fun onBindViewHolder(holder: ForumAnnouncementViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ForumAnnouncementViewHolder(private var binding: ForumAnnouncementBinding, private var context: Context)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(threadPreview: ThreadPreview){
            binding.apply{
                tag.text = threadPreview.tag
                time.text = threadPreview.time
                previewText.text = threadPreview.text
                likesText.text = threadPreview.likes.toString()
                commentsText.text = threadPreview.comment_cnt.toString()
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