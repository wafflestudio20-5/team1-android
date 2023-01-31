package com.waffle22.wafflytime.ui.notification.chat.room

import android.os.Message
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.MyMessageItemBinding
import com.waffle22.wafflytime.databinding.OppoMessageItemBinding
import com.waffle22.wafflytime.network.dto.ChatSimpleInfo
import com.waffle22.wafflytime.network.dto.MessageInfo

class ChatRoomAdapter(): ListAdapter<MessageInfo, RecyclerView.ViewHolder>(DiffCallback) {
    class MyMessageViewHolder(
        private val binding: MyMessageItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(messageInfo: MessageInfo) {
            binding.messageText.text = messageInfo.content
            binding.timeText.text = itemView.context.getString(R.string.message_time).format(
                messageInfo.sentAt.hour, messageInfo.sentAt.minute
            )
        }
    }

    class OppoMessageViewHolder(
        private val binding: OppoMessageItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(messageInfo: MessageInfo) {
            binding.messageText.text = messageInfo.content
            binding.timeText.text = itemView.context.getString(R.string.message_time).format(
                messageInfo.sentAt.hour, messageInfo.sentAt.minute
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(getItem(position).received) OPPO_MESSAGE else MY_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            MY_MESSAGE -> MyMessageViewHolder(
                MyMessageItemBinding.inflate(layoutInflater, parent, false)
            )
            else -> OppoMessageViewHolder(
                OppoMessageItemBinding.inflate(layoutInflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType) {
            MY_MESSAGE -> {
                val myMessageHolder = holder as MyMessageViewHolder
                myMessageHolder.bind(getItem(position))
            }
            else -> {
                val oppoMessageHolder = holder as OppoMessageViewHolder
                oppoMessageHolder.bind(getItem(position))
            }
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<MessageInfo>() {
        override fun areItemsTheSame(
            oldItem: MessageInfo,
            newItem: MessageInfo
        ): Boolean {
            return (oldItem.sentAt == newItem.sentAt)   //id값이 없어서 sentAt을 대신 사용
        }

        override fun areContentsTheSame(
            oldItem: MessageInfo,
            newItem: MessageInfo
        ): Boolean {
            return oldItem == newItem
        }

        const val MY_MESSAGE = 0
        const val OPPO_MESSAGE = 1
    }
}