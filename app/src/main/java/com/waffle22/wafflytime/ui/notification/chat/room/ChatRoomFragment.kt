package com.waffle22.wafflytime.ui.notification.chat.room

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.FragmentChatRoomBinding
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ChatRoomFragment: Fragment() {
    private lateinit var binding: FragmentChatRoomBinding
    private val navigationArgs: ChatRoomFragmentArgs by navArgs()
    private val viewModel: ChatRoomViewModel by viewModel {
        parametersOf(navigationArgs.chatId)
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var messagesAdapter: ChatRoomAdapter

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
        binding.toolBar.title = navigationArgs.chatTarget
        binding.toolBar.setNavigationOnClickListener { findNavController().navigateUp() }
        recyclerView = binding.chatRoomRecyclerView
        messagesAdapter = ChatRoomAdapter()
        recyclerView.adapter = messagesAdapter
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(-1)) {
                    getMessages()
                }
            }
        })
        binding.sendMessageButton.setOnClickListener {
            sendMessage()
        }
    }

    override fun onStart() {
        super.onStart()
        getMessages()
    }

    fun getMessages() {
        viewModel.getMessages()
        lifecycleScope.launch {
            viewModel.messagesState.collect {
                if(it.status != "0") {
                    if(it.status == "200") {
                        messagesAdapter.submitList(it.dataHolder)
                    }
                    else {
                        Toast.makeText(context, it.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    coroutineContext.job.cancel()
                }
            }
        }
    }

    fun sendMessage() {
        val content = binding.messageInputText.editText!!.text.toString()
        if(content.isEmpty()) return
        binding.messageInputText.editText!!.text.clear()
        viewModel.sendMessage(content)
        lifecycleScope.launch {
            viewModel.messagesState.collect {
                if(it.status != "0") {
                    if(it.status == "200") {
                        messagesAdapter.submitList(it.dataHolder)
                        recyclerView.scrollToPosition(messagesAdapter.itemCount-1)
                    }
                    else {
                        Toast.makeText(context, it.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    coroutineContext.job.cancel()
                }
            }
        }
    }

}