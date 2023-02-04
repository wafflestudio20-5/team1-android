package com.waffle22.wafflytime.ui.boards.post

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
                .asBitmap()
                .load(image.preSignedUrl)
                .placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_24)
                .fallback(R.drawable.ic_baseline_person_24)
                .into(binding.image)
            binding.image.setOnClickListener{
                val dialog = AlertDialog.Builder(context)
                val factory = LayoutInflater.from(context)
                val customView = factory.inflate(R.layout.post_image_detail,null)
                val imageView = customView.findViewById<ImageView>(R.id.image)
                Glide.with(context)
                    .load(image.preSignedUrl)
                    .into(imageView)
                customView.findViewById<TextView>(R.id.description).text = image.description
                dialog.setView(customView)
                dialog.setPositiveButton("닫기"){
                    _, _ -> null
                }
                dialog.show()
            }
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