package com.waffle22.wafflytime.ui.boards.post.newpost

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.NewPostImageBinding
import com.waffle22.wafflytime.network.dto.ImageRequest
import com.bumptech.glide.Glide

class NewPostImageAdapter(
    private val onEditDescription: (ImageRequest) -> Unit,
    private val onDeleteImage: (ImageRequest) -> Unit
)
    : ListAdapter<ImageStorage, NewPostImageAdapter.NewPostImageViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewPostImageViewHolder {
        return NewPostImageViewHolder(
            NewPostImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), parent.context
        )
    }

    override fun onBindViewHolder(holder: NewPostImageViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, onEditDescription, onDeleteImage)
    }

    class NewPostImageViewHolder(private var binding: NewPostImageBinding, private val context: Context)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(
            image: ImageStorage,
            onEditDescription: (ImageRequest) -> Unit,
            onDeleteImage: (ImageRequest) -> Unit
        ){
            Glide.with(context)
                .asBitmap()
                .load(image.byteArray)
                .into(binding.image)
            binding.addDescription.setOnClickListener {
                onEditDescription(image.imageRequest)
            }
            binding.deleteImage.setOnClickListener {
                onDeleteImage(image.imageRequest)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ImageStorage>() {
            override fun areContentsTheSame(
                oldItem: ImageStorage,
                newItem: ImageStorage
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(
                oldItem: ImageStorage,
                newItem: ImageStorage
            ): Boolean {
                return oldItem.imageRequest.imageId == newItem.imageRequest.imageId
            }
        }
    }
}