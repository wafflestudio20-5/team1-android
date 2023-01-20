package com.waffle22.wafflytime.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.waffle22.wafflytime.databinding.FragmentChatboxBinding
import com.waffle22.wafflytime.network.dto.Chat

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
        }


    }
}