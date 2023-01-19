package com.waffle22.wafflytime.ui.boards.boardscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.BoardAnnouncementBinding
import com.waffle22.wafflytime.network.dto.PostResponse
import com.waffle22.wafflytime.network.dto.TimeDTO
import java.time.LocalDate

class BoardAnnouncementAdapter(private val clicked: (PostResponse) -> Unit)
    : ListAdapter<PostResponse, BoardAnnouncementAdapter.ForumAnnouncementViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumAnnouncementViewHolder {
        return ForumAnnouncementViewHolder(
            BoardAnnouncementBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), parent.context
        )
    }

    override fun onBindViewHolder(holder: ForumAnnouncementViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, clicked)
    }

    class ForumAnnouncementViewHolder(private var binding: BoardAnnouncementBinding, private var context: Context)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(postAbstract: PostResponse, clicked: (PostResponse) -> Unit){
            binding.apply{
                tag.text = "질문"
                time.text = timeToText(postAbstract.createdAt)
                previewText.text = postAbstract.title ?: postAbstract.contents
                likesText.text = postAbstract.nlikes.toString()
                commentsText.text = postAbstract.nreplies.toString()
                layout.setOnClickListener{clicked(postAbstract)}
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
        private val DiffCallback = object : DiffUtil.ItemCallback<PostResponse>() {
            override fun areContentsTheSame(
                oldItem: PostResponse,
                newItem: PostResponse
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: PostResponse, newItem: PostResponse): Boolean {
                return oldItem.postId == newItem.postId
            }
        }
    }
}