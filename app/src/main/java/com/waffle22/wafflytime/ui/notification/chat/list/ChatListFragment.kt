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
import com.waffle22.wafflytime.util.PagingLoadStateAdapter
import com.waffle22.wafflytime.util.SlackState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChatListFragment : Fragment() {
    private val viewModel: ChatListViewModel by sharedViewModel()
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
//        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver(){
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int){
//                if (positionStart == 0){
//                    recyclerView.scrollToPosition(0)
//                }
//            }
//        })
        recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.chatListPager.collectLatest {
                    pagingData -> adapter.submitData(pagingData)
            }
        }
        adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter(adapter::retry),
            footer = PagingLoadStateAdapter(adapter::retry)
        )
//        binding.swipeRefreshLayout.setOnRefreshListener { showChatList() }
    }

    //BottomNavigation으로 다른 fragment로 갔다가 다시 왔을 때 실행되지 않음(소켓 통신 구현하며 해결 예정)
    override fun onStart() {
        super.onStart()
//        showChatList()
    }

//    private fun showChatList() {
//        viewModel.getChatList()
//        lifecycleScope.launch {
//            viewModel.chatBoxState.collect {
//                if(it.status != "0") {
//                    when(it.status) {
//                        "200" -> {
//                            adapter.submitList(it.dataHolder)
//                        }
//                        else -> {
//                            Toast.makeText(context, it.errorMessage, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    viewModel.resetState()
//                    binding.swipeRefreshLayout.isRefreshing = false
//                    coroutineContext.job.cancel()
//                }
//            }
//        }
//    }

    private fun moveToChatRoom(chatSimpleInfo: ChatSimpleInfo) {
        baseNotificationViewModel.setStateChatRoom(chatSimpleInfo)
    }
}