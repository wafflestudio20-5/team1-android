package com.waffle22.wafflytime.ui.notification.chat.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.ChatboxItemBinding
import com.waffle22.wafflytime.network.dto.ChatSimpleInfo

class ChatListAdapter(
    private val onClickedChat: (ChatSimpleInfo) -> Unit
): PagingDataAdapter<ChatSimpleInfo, ChatListAdapter.ChatViewHolder>(DiffCallback){

    class ChatViewHolder(
        private val binding: ChatboxItemBinding,
        private val onClickedChat: (ChatSimpleInfo) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(chatSimpleInfo: ChatSimpleInfo?) {
            chatSimpleInfo?.let {
                binding.apply {
                    commentTitle.text = chatSimpleInfo.target
                    commentContent.text = chatSimpleInfo.recentMessage
                    if(chatSimpleInfo.unread > 0) {
                        unreadNum.text = chatSimpleInfo.unread.toString()
                    } else {
                        unreadNum.visibility = View.GONE
                    }
                    commentTime.text = "No Data from the server"
                    root.setOnClickListener { onClickedChat(chatSimpleInfo) }
                    executePendingBindings()
                }
            }
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