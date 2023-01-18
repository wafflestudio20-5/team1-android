package com.waffle22.wafflytime.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.ChatboxItemBinding
import com.waffle22.wafflytime.network.dto.Chat

class ChatAdapter: ListAdapter<Chat, ChatAdapter.ChatViewHolder>(DiffCallback){

    class ChatViewHolder(
        private val binding: ChatboxItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            binding.apply {
                commentTitle.text = chat.Name
                commentContent.text = chat.content
                commentTime.text = chat.date
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ChatViewHolder(
            ChatboxItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val getChat = getItem(position)
        holder.bind(getChat)
    }
    companion object DiffCallback: DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.Name == newItem.Name
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

    }




}