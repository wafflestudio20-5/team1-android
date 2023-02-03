package com.waffle22.wafflytime.ui.notification.chat.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.FragmentChatboxBinding
import com.waffle22.wafflytime.network.dto.ChatSimpleInfo
import com.waffle22.wafflytime.ui.notification.BaseNotificationViewModel
import com.waffle22.wafflytime.ui.notification.chat.ChatViewModel
import com.waffle22.wafflytime.util.PagingLoadStateAdapter
import com.waffle22.wafflytime.util.SlackState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChatListFragment : Fragment() {
    private val viewModel: ChatViewModel by sharedViewModel()
    private val baseNotificationViewModel: BaseNotificationViewModel by sharedViewModel()
    private lateinit var binding : FragmentChatboxBinding
    private lateinit var adapter: ChatListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatboxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.chatBoxRecyclerView
        adapter = ChatListAdapter { moveToChatRoom(it) }
        recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.chatList.collect {
                adapter.submitList(it)
            }
        }
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)) {
                    getChatList()
                }
            }
        })
        getChatList()
        viewModel.startWebSocket()
    }

    fun getChatList() {
        viewModel.getChatList()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.chatListState.collect {
                if(it.status != "0") {
                    if(it.status != "200") {
                        Toast.makeText(context, it.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    coroutineContext.job.cancel()
                }
            }
        }
    }

    private fun moveToChatRoom(chatSimpleInfo: ChatSimpleInfo) {
        baseNotificationViewModel.setStateChatRoom(chatSimpleInfo)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CHATLIST", "onDestroy()")
    }
}