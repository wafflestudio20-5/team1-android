package com.waffle22.wafflytime.ui.boards.post

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.PostImageBinding
import com.waffle22.wafflytime.network.dto.ImageResponse
import com.bumptech.glide.Glide

class PostImageAdapter(): ListAdapter<ImageResponse, PostImageAdapter.PostImageViewHolder>(DiffCallback){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageViewHolder{
        return PostImageViewHolder(
            PostImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), parent.context
        )
    }

    override fun onBindViewHolder(holder: PostImageViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class PostImageViewHolder(private var binding: PostImageBinding, private val context : Context)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(image:ImageResponse){
            Glide.with(context)
                .load(image.preSignedUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .fallback(R.drawable.ic_person)
                .into(binding.image)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ImageResponse>() {
            override fun areContentsTheSame(
                oldItem: ImageResponse,
                newItem: ImageResponse
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(
                oldItem: ImageResponse,
                newItem: ImageResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}