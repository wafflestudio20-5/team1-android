package com.waffle22.wafflytime.ui.notification.chat.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.waffle22.wafflytime.databinding.FragmentChatRoomBinding

class ChatRoomFragment: Fragment() {
    private lateinit var binding: FragmentChatRoomBinding
    private val navigationArgs: ChatRoomFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolBar.title = navigationArgs.chatId.toString()
        binding.toolBar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

}