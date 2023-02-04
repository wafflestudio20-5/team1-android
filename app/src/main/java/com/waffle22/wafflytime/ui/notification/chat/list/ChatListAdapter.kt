package com.waffle22.wafflytime.ui.notification.chat.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.ChatboxItemBinding
import com.waffle22.wafflytime.network.dto.ChatSimpleInfo
import com.waffle22.wafflytime.util.timeToString

class ChatListAdapter(
    private val onClickedChat: (ChatSimpleInfo) -> Unit
): ListAdapter<ChatSimpleInfo, ChatListAdapter.ChatViewHolder>(DiffCallback){

    class ChatViewHolder(
        private val binding: ChatboxItemBinding,
        private val onClickedChat: (ChatSimpleInfo) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(chatSimpleInfo: ChatSimpleInfo?) {
            chatSimpleInfo?.let {
                binding.apply {
                    commentTitle.text = chatSimpleInfo.target
                    commentContent.text = chatSimpleInfo.recentMessage
                    unreadNum.text = chatSimpleInfo.unread.toString()
                    if(chatSimpleInfo.unread == 0) {
                        unreadNum.visibility = View.INVISIBLE
                        commentContent.setTextAppearance(R.style.chatReadContentText)
                    } else {
                        unreadNum.visibility = View.VISIBLE
                        commentContent.setTextAppearance(R.style.chatUnreadContentText)
                    }
                    commentTime.text = chatSimpleInfo.recentTime?.timeToString()
                    root.setOnClickListener { onClickedChat(chatSimpleInfo) }
//                    executePendingBindings()
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
            return (oldItem.id == newItem.id)
        }

        override fun areContentsTheSame(
            oldItem: ChatSimpleInfo,
            newItem: ChatSimpleInfo
        ): Boolean {
            return oldItem == newItem
        }

    }
}