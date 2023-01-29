package com.waffle22.wafflytime.ui.notification.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.waffle22.wafflytime.databinding.FragmentChatboxBinding
import com.waffle22.wafflytime.network.dto.Chat
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChatBoxFragment : Fragment() {
    private val viewModel: ChatBoxViewModel by sharedViewModel()
    private lateinit var binding : FragmentChatboxBinding
    private lateinit var adapter: ChatAdapter

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

        viewModel.

        adapter = ChatAdapter()
        var hello: List<Chat> = listOf(Chat("diluc","sword","0101"), Chat("pai","cute","12"))
        adapter.submitList(hello)

        binding.apply {
            chatBoxRecyclerView.adapter = adapter
        }

    }
}