package com.waffle22.wafflytime.ui.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.waffle22.wafflytime.databinding.FragmentBaseNotificationBinding
import com.waffle22.wafflytime.network.dto.ChatSimpleInfo
import com.waffle22.wafflytime.network.dto.NotificationData
import com.waffle22.wafflytime.ui.notification.chat.list.ChatListFragment
import com.waffle22.wafflytime.ui.notification.notify.NotifyFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BaseNotificationFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var binding: FragmentBaseNotificationBinding
    private lateinit var pageAdapter: BaseNotificationPageAdapter

    private val viewModel: BaseNotificationViewModel by sharedViewModel()
    private val tabTitleList = listOf("알림","쪽지")
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("debug","show?")
        binding = FragmentBaseNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pageAdapter = BaseNotificationPageAdapter(this.requireActivity())
        pageAdapter.addItems(NotifyFragment())
        pageAdapter.addItems(ChatListFragment())
        viewPager = binding.viewPager
        viewPager.adapter = pageAdapter
        TabLayoutMediator(binding.topTap, viewPager) { tab, position ->
            tab.text = tabTitleList[position]
        }.attach()

        lifecycleScope.launch {
            viewModel.navigationState.collect {
                navigationLogic(it)
            }
        }
    }

    private fun navigationLogic(state: BaseNotificationState) {
        viewModel.resetState()
        when(state.navigationState) {
            NavigationState.StanBy -> {
                null
            }
            NavigationState.Post -> {
                val postData = (state.dataHolder as NotificationData).notificationInfo
                val action = BaseNotificationFragmentDirections.actionBaseNotificationFragmentToPostFragment(postData.boardId, postData.postId)
                findNavController().navigate(action)
            }
            NavigationState.ChatRoom -> {
                val chatRoomData = state.dataHolder as ChatSimpleInfo
                val action = BaseNotificationFragmentDirections.actionBaseNotificationFragmentToChatRoomFragment(chatRoomData.id, chatRoomData.target, chatRoomData.blocked)
                findNavController().navigate(action)
            }
        }

    }
}