package com.waffle22.wafflytime.ui.boards.postscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.PostReplyBinding
import com.waffle22.wafflytime.network.dto.ReplyResponse
//import com.waffle22.wafflytime.network.dto.TimeDTO
//import java.time.LocalDate

class PostReplyAdapter(
    private val replyClicked: (ReplyResponse) -> Unit,
    private val editable: (ReplyResponse) -> Boolean,
    private val modifyReply: (Boolean, ReplyResponse) -> Unit,
    private val moveToNewChat: (ReplyResponse) -> Unit
) : ListAdapter<ReplyResponse, PostReplyAdapter.PostReplyViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostReplyViewHolder {
        return PostReplyViewHolder(
            PostReplyBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), parent.context
        )
    }

    override fun onBindViewHolder(holder: PostReplyViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, replyClicked, editable, modifyReply, moveToNewChat)
    }

    class PostReplyViewHolder(private var binding: PostReplyBinding, private var context: Context)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(reply: ReplyResponse,
                 replyClicked: (ReplyResponse) -> Unit,
                 editable: (ReplyResponse) -> Boolean,
                 modifyReply: (Boolean, ReplyResponse) -> Unit,
                 moveToNewChat: (ReplyResponse) -> Unit) {
            binding.apply{
                nickname.text = if(reply.nickname == "익명" && reply.isPostWriter)"익명(글쓴이)" else reply.nickname
                //time.text = ""
                replyText.text = reply.contents
                //likesText.text = reply.
                if (reply.isRoot) notRoot.visibility = View.GONE
                else replyButton.visibility = View.GONE
                replyButton.setOnClickListener { replyClicked(reply) }
                moreButton.setOnClickListener {
                    val popupMenu = PopupMenu(context, moreButton)
                    popupMenu.menuInflater.inflate(R.menu.reply_actions, popupMenu.menu)
                    if(editable(reply)){
                        popupMenu.menu.findItem(R.id.dm).isEnabled = false
                    }
                    else{
                        popupMenu.menu.findItem(R.id.edit).isEnabled = false
                        popupMenu.menu.findItem(R.id.delete).isEnabled = false
                    }
                    popupMenu.setOnMenuItemClickListener { item ->
                        when(item.itemId){
                            R.id.notification -> {}
                            R.id.dm -> {moveToNewChat(reply)}
                            R.id.edit -> {modifyReply(true, reply)}
                            R.id.delete -> {modifyReply(false, reply)}
                        }
                        true
                    }
                    popupMenu.show()
                }
            }
        }
        /*private fun timeToText(time: TimeDTO): String{
            var timeText = time.month.toString() + '/' + time.day.toString() + ' ' + time.hour.toString() + ':' + time.minute.toString()
            if (LocalDate.now().year != time.year)
                timeText = time.year.toString() + '/' + timeText
            return timeText
        }*/
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ReplyResponse>() {
            override fun areContentsTheSame(
                oldItem: ReplyResponse,
                newItem: ReplyResponse
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(
                oldItem: ReplyResponse,
                newItem: ReplyResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}