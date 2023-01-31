package com.waffle22.wafflytime.ui.boards.boardscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.BoardAnnouncementBinding
import com.waffle22.wafflytime.databinding.BoardThreadBinding
import com.waffle22.wafflytime.network.dto.PostResponse
import com.waffle22.wafflytime.network.dto.TimeDTO
import java.time.LocalDate

class PostPreviewAdapter(
    private val clicked: (PostResponse) -> Unit
): ListAdapter<PostResponse, RecyclerView.ViewHolder>(DiffCallback){

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isQuestion) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) BoardAnnouncementViewHolder(
            BoardAnnouncementBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        else PostAbstractViewHolder(
                BoardThreadBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val current = getItem(position)
        if (current.isQuestion) (holder as BoardAnnouncementViewHolder).bind(current, clicked)
        else (holder as PostAbstractViewHolder).bind(current, clicked)
    }

    class PostAbstractViewHolder(private var binding: BoardThreadBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(postAbstract: PostResponse, clicked: (PostResponse) -> Unit) {
            binding.apply{
                nickname.text = postAbstract.nickname ?: "익명"
                time.text = timeToText(postAbstract.createdAt)
                previewText.text = postAbstract.contents
                likesText.text = postAbstract.nlikes.toString()
                commentsText.text = postAbstract.nreplies.toString()
                if (postAbstract.title != null) title.text = postAbstract.title
                else    title.visibility = View.GONE
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

    class BoardAnnouncementViewHolder(private var binding: BoardAnnouncementBinding)
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