package com.waffle22.wafflytime.util

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.NotificationItemBinding
import com.waffle22.wafflytime.network.dto.Notification

class NotifyAdapter: ListAdapter<Notification, NotifyAdapter.NotifyViewHolder>(DiffCallback){

    class NotifyViewHolder(
        private val binding: NotificationItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            binding.apply {
                commentTitle.text = notification.title
                commentContent.text = notification.content
                commentTime.text = notification.date
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return NotifyViewHolder(
            NotificationItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotifyViewHolder, position: Int) {
        val getNotification = getItem(position)
        holder.bind(getNotification)
    }
    companion object DiffCallback: DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }

    }




}