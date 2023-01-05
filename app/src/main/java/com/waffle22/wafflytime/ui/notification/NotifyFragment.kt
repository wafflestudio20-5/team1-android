package com.waffle22.wafflytime.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        }


    }
}