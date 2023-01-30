package com.waffle22.wafflytime.ui.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.waffle22.wafflytime.databinding.FragmentBaseNotificationBinding
import com.waffle22.wafflytime.ui.notification.chat.ChatBoxFragment
import com.waffle22.wafflytime.ui.notification.notify.NotifyFragment

class BaseNotificationFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var binding: FragmentBaseNotificationBinding
    private lateinit var pageAdapter: NotificationPageAdapter

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
        pageAdapter = NotificationPageAdapter(this.requireActivity())
        pageAdapter.addItems(NotifyFragment())
        pageAdapter.addItems(ChatBoxFragment())
        viewPager = binding.viewPager
        viewPager.adapter = pageAdapter
        TabLayoutMediator(binding.topTap, viewPager) { tab, position ->
            tab.text = tabTitleList[position]
        }.attach()

    }
}