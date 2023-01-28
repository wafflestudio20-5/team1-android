package com.waffle22.wafflytime.ui.boards.newchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentNewchatBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewChatFragment: Fragment() {
    private lateinit var binding: FragmentNewchatBinding
    private val viewModel: NewChatViewModel by sharedViewModel()
    private val navigationArgs: NewChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewchatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.btn_sendNewChat -> {
                    viewModel.sendNewChat(navigationArgs.boardId, navigationArgs.postId, false, binding.editTextFirstChat.text.toString())
                    findNavController().navigateUp()
                    true
                }
                else -> false
            }
        }
    }
}