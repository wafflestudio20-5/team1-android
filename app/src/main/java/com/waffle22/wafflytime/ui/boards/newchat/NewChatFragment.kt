package com.waffle22.wafflytime.ui.boards.newchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentNewchatBinding
import com.waffle22.wafflytime.ui.login.SignUpFragmentDirections
import com.waffle22.wafflytime.util.StateStorage
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewChatFragment: Fragment() {
    private lateinit var binding: FragmentNewchatBinding
    private lateinit var alertDialog: AlertDialog
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

        binding.apply {
            toolBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            toolBar.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.btn_sendNewChat -> {
                        val content = binding.editTextFirstChat.text.toString()
                        if (content.isNotEmpty()){
                            verifySendMessage()
                            true
                        } else {
                            Toast.makeText(context, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                            false
                        }

                    }
                    else -> false
                }
            }
        }

        lifecycleScope.launch {
            viewModel.newChatState.collect {
                newChatLogic(it)
            }
        }
    }

    private fun verifySendMessage() {
        val verifyDialog = MaterialAlertDialogBuilder(this.requireContext())
            // TODO: 익명 확인 추가
            .setMessage("정말 쪽지를 보내시겠어요?")
            .setPositiveButton("예") { dialog, which ->
                // 메세지 보냄
                sendMessage()
            }
            .setNegativeButton("아니요") { _, _ -> null }
            .show()
        verifyDialog.setCanceledOnTouchOutside(false)
    }

    private fun sendMessage() {
        alertDialog = MaterialAlertDialogBuilder(this.requireContext())
            .setView(ProgressBar(this.requireContext()))
            .setMessage("Sending...")
            .show()
        alertDialog.setCanceledOnTouchOutside(false)

        val isAnonymous = binding.isAnonymous.isChecked
        val content = binding.editTextFirstChat.text.toString()
        viewModel.sendNewChat(navigationArgs.boardId, navigationArgs.postId, navigationArgs.replyId,isAnonymous, content)
    }

    private fun newChatLogic(state: StateStorage){
        when(state.status) {
            // 대기 상태
            "0" -> null
            else -> {
                alertDialog.dismiss()
                when(state.status) {
                    // 쪽지 전송 성공
                    "200" -> {
                        Toast.makeText(this.requireContext(), "쪽지 전송이 완료되었습니다", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                    // 시스템 에러
                    else -> {
                        Toast.makeText(this.requireContext(), state.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.resetState()
            }
        }
    }
}