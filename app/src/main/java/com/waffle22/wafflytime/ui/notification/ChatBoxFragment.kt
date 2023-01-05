package com.waffle22.wafflytime.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentChatboxBinding
import com.waffle22.wafflytime.databinding.FragmentNotificationBinding
import com.waffle22.wafflytime.network.dto.Chat
import com.waffle22.wafflytime.network.dto.Notification
import com.waffle22.wafflytime.util.ChatAdapter
import com.waffle22.wafflytime.util.NotifyAdapter

class ChatBoxFragment : Fragment() {
    private lateinit var binding : FragmentChatboxBinding

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

        val adapter = ChatAdapter()
        var hello: List<Chat> = listOf(Chat("diluc","sword","0101"), Chat("pai","cute","12"))
        adapter.submitList(hello)

        binding.apply {
            chatBoxRecyclerView.adapter = adapter
            topTap.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    when(tab.position){
                        0 -> findNavController().navigate(R.id.action_chatBoxFragment_to_notifyFragment)
                        else -> null
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }


    }
}