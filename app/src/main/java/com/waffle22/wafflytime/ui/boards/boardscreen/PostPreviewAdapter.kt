package com.waffle22.wafflytime.ui.boards.boardscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waffle22.wafflytime.databinding.BoardAnnouncementBinding
import com.waffle22.wafflytime.databinding.BoardThreadBinding
import com.waffle22.wafflytime.network.dto.PostResponse
import com.waffle22.wafflytime.network.dto.TimeDTO
import com.waffle22.wafflytime.util.previewText
import com.waffle22.wafflytime.util.timeToString
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
                ), parent.context
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val current = getItem(position)
        if (current.isQuestion) (holder as BoardAnnouncementViewHolder).bind(current, clicked)
        else (holder as PostAbstractViewHolder).bind(current, clicked)
    }

    class PostAbstractViewHolder(private var binding: BoardThreadBinding, private val context: Context)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            postAbstract: PostResponse,
            clicked: (PostResponse) -> Unit
        ) {
            binding.apply{
                nickname.text = postAbstract.nickname ?: "익명"
                time.text = postAbstract.createdAt.timeToString()
                previewText.text = postAbstract.contents.previewText(3)
                likesText.text = postAbstract.nlikes.toString()
                commentsText.text = postAbstract.nreplies.toString()
                if (postAbstract.title != null) title.text = postAbstract.title
                else    title.visibility = View.GONE
                if(!postAbstract.images.isNullOrEmpty()){
                    imagePreview.visibility = View.VISIBLE
                    Glide.with(context)
                        .asBitmap()
                        .load(postAbstract.images[0].preSignedUrl)
                        .into(imagePreview)
                }
                else {
                    imagePreview.visibility = View.GONE
                }
                boardName.text = postAbstract.boardTitle
                layout.setOnClickListener{clicked(postAbstract)}
            }
        }
    }

    class BoardAnnouncementViewHolder(private var binding: BoardAnnouncementBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(postAbstract: PostResponse, clicked: (PostResponse) -> Unit){
            binding.apply{
                tag.text = "질문"
                time.text = postAbstract.createdAt.timeToString()
                previewText.text = postAbstract.title ?: postAbstract.contents.previewText(2)
                likesText.text = postAbstract.nlikes.toString()
                commentsText.text = postAbstract.nreplies.toString()
                layout.setOnClickListener{clicked(postAbstract)}
            }
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