package com.waffle22.wafflytime.ui.notification.notify

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.LoadingItemBinding
import com.waffle22.wafflytime.databinding.NotificationItemBinding
import com.waffle22.wafflytime.network.dto.NotificationData

class NotifyAdapter(
    private val moveToPost: (NotificationData) -> Unit
): ListAdapter<NotificationData, NotifyAdapter.NotifyViewHolder>(DiffCallback){

    class NotifyViewHolder(
        private val binding: NotificationItemBinding,
        private val moveToPost: (NotificationData) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(notificationData: NotificationData) {
            binding.apply {
                commentTitle.text = notificationData.notificationType
                commentContent.text = notificationData.notificationContent
                commentTime.text = "hi"
                root.setOnClickListener { moveToPost(notificationData) }
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return NotifyViewHolder(
                NotificationItemBinding.inflate(layoutInflater, parent, false),
                moveToPost
            )
    }

    override fun onBindViewHolder(holder: NotifyViewHolder, position: Int) {
        val getNotification = getItem(position)
        holder.bind(getNotification)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<NotificationData>() {
        override fun areItemsTheSame(
            oldItem: NotificationData,
            newItem: NotificationData
        ): Boolean {
            return (oldItem.notificationContent == newItem.notificationContent) && (oldItem.notificationCreatedAt == newItem.notificationCreatedAt)
        }

        override fun areContentsTheSame(
            oldItem: NotificationData,
            newItem: NotificationData
        ): Boolean {
            return oldItem == newItem
        }

    }

}
/*
class DiffCallback(
    private val oldItems: List<Any>,
    private val newItems: List<Any>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        TODO("Not yet implemented")
    }

    override fun getNewListSize(): Int {
        TODO("Not yet implemented")
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        TODO("Not yet implemented")
    }

}
 */