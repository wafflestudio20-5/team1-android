package com.waffle22.wafflytime.ui.notification.chat.room

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentChatRoomBinding
import com.waffle22.wafflytime.ui.notification.chat.ChatViewModel
import com.waffle22.wafflytime.util.PagingLoadStateAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.net.Socket

class ChatRoomFragment: Fragment() {
    private lateinit var binding: FragmentChatRoomBinding
    private val navigationArgs: ChatRoomFragmentArgs by navArgs()
    private val viewModel: ChatViewModel by sharedViewModel()
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
        viewModel.setupChatRoom(navigationArgs.chatId)
        binding.apply {
            toolBar.title = navigationArgs.chatTarget
            toolBar.setNavigationOnClickListener { findNavController().navigateUp() }
            toolBar.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.block_menu_item -> {
                        blockChatRoom()
                        true
                    }
                    R.id.unblock_menu_item -> {
                        unblockChatRoom()
                        true
                    }
                    else -> false
                }
            }
            sendMessageButton.setOnClickListener {
                sendMessage()
            }
        }
        setBlockMenu()

        recyclerView = binding.chatRoomRecyclerView
        messagesAdapter = ChatRoomAdapter()
        recyclerView.adapter = messagesAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.messageList.collect {
                messagesAdapter.submitList(it)
            }
        }
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(-1)) {
                    getMessages()
                }
            }
        })
        getMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearChatRoom()
    }

    fun getMessages() {
        viewModel.getMessages()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.messagesState.collect {
                if(it.status != "0") {
                    if(it.status != "200") {
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
    }

    fun blockChatRoom() {
        viewModel.blockChatRoom(true)
        lifecycleScope.launch {
            viewModel.blockState.collect {
                if(it.status != "0") {
                    if(it.status == "200") {
                        Toast.makeText(context, "채팅방을 차단하였습니다.", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                    else {
                        Toast.makeText(context, it.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    coroutineContext.job.cancel()
                }
            }
        }
    }

    fun unblockChatRoom() {
        viewModel.blockChatRoom(false)
        lifecycleScope.launch {
            viewModel.blockState.collect {
                if(it.status != "0") {
                    if(it.status == "200") {
                        Toast.makeText(context, "채팅방을 차단 해제하였습니다.", Toast.LENGTH_SHORT).show()
                        setBlockMenu()
                    }
                    else {
                        Toast.makeText(context, it.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    coroutineContext.job.cancel()
                }
            }
        }
    }

    fun setBlockMenu() {
        binding.apply {
            toolBar.menu.getItem(0).isVisible = !viewModel.curChat!!.blocked
            toolBar.menu.getItem(1).isVisible = viewModel.curChat!!.blocked
        }
    }
}