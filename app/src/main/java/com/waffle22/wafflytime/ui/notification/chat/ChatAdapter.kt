package com.waffle22.wafflytime.ui.notification.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.ChatboxItemBinding
import com.waffle22.wafflytime.network.dto.Chat
import com.waffle22.wafflytime.network.dto.ChatSimpleInfo
import com.waffle22.wafflytime.network.dto.NotificationData

class ChatAdapter(
    private val onClickedChat: (ChatSimpleInfo) -> Unit
): ListAdapter<ChatSimpleInfo, ChatAdapter.ChatViewHolder>(DiffCallback){

    class ChatViewHolder(
        private val binding: ChatboxItemBinding,
        private val onClickedChat: (ChatSimpleInfo) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(chatSimpleInfo: ChatSimpleInfo) {
            binding.apply {
                commentTitle.text = chatSimpleInfo.id.toString()
                commentContent.text = chatSimpleInfo.recentMessage
                commentTime.text = "No Data from the server"
                root.setOnClickListener { onClickedChat(chatSimpleInfo) }
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ChatViewHolder(
            ChatboxItemBinding.inflate(layoutInflater, parent, false), onClickedChat
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatSimpleInfo = getItem(position)
        holder.bind(chatSimpleInfo)
    }
    companion object DiffCallback: DiffUtil.ItemCallback<ChatSimpleInfo>() {
        override fun areItemsTheSame(
            oldItem: ChatSimpleInfo,
            newItem: ChatSimpleInfo
        ): Boolean {
            return (oldItem.id == newItem.id) && (oldItem.recentMessage == newItem.recentMessage)
        }

        override fun areContentsTheSame(
            oldItem: ChatSimpleInfo,
            newItem: ChatSimpleInfo
        ): Boolean {
            return oldItem == newItem
        }

    }




}