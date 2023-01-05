package com.waffle22.wafflytime.ui.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentNotificationBinding
import com.waffle22.wafflytime.network.dto.Notification
import com.waffle22.wafflytime.util.NotifyAdapter

class NotifyFragment : Fragment() {
    private lateinit var binding : FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = NotifyAdapter()
        var hello: List<Notification> = listOf(Notification("hi","hello","0101"), Notification("2","sfdfs","121"))
        adapter.submitList(hello)

        binding.apply {
            commentRecyclerView.adapter = adapter
            /*
            topTap.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    Log.d("debug",tab.position.toString())
                    when(tab.position){
                        1 -> findNavController().navigate(R.id.action_notifyFragment_to_chatBoxFragment)
                        else -> null
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
            *?
             */
        }


    }
}