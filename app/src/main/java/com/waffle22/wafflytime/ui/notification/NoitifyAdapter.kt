package com.waffle22.wafflytime.ui.notification

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.LoadingItemBinding
import com.waffle22.wafflytime.databinding.NotificationItemBinding
import com.waffle22.wafflytime.network.dto.NotificationData

class NotifyAdapter: ListAdapter<NotificationData, NotifyAdapter.NotifyViewHolder>(DiffCallback){

    class NotifyViewHolder(
        private val binding: NotificationItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(notificationData: NotificationData) {
            binding.apply {
                commentTitle.text = notificationData.notificationType
                commentContent.text = notificationData.notificationContent
                commentTime.text = "hi"
            }
            binding.executePendingBindings()
        }
    }
    /*
    class LoadingViewHolder(
        private val binding: LoadingItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

    }
    */

    /*
    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).notificationType == "null") 1 else 0
    }
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return NotifyViewHolder(
                NotificationItemBinding.inflate(layoutInflater, parent, false)
            )

        /*
        return if (viewType == 0) {
            NotifyViewHolder(
                NotificationItemBinding.inflate(layoutInflater, parent, false)
            )
        } else {
            LoadingViewHolder(
                LoadingItemBinding.inflate(layoutInflater, parent, false)
            )
        }
         */
    }

    override fun onBindViewHolder(holder: NotifyViewHolder, position: Int) {
        val getNotification = getItem(position)
        holder.bind(getNotification)
        /*
        if (getNotification.notificationType != "null") {
            (holder as NotifyViewHolder).bind(getNotification)
        } else {
            // Loading View Holder Binding, Currently nothing
        }
         */
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