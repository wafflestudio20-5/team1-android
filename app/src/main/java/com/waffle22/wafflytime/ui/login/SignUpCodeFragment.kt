package com.waffle22.wafflytime.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.waffle22.wafflytime.databinding.FragmentSignupEmailCodeBinding
import com.waffle22.wafflytime.util.SlackState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignUpCodeFragment: Fragment() {
    private lateinit var binding: FragmentSignupEmailCodeBinding
    private lateinit var alertDialog: AlertDialog
    private val viewModel: SignUpEmailViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupEmailCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply{
            btnCheckEmailCode.setOnClickListener { codeVerify() }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.signUpCodeState.collect {
                signUpCodeLogic(it)
            }
        }
    }

    private fun codeVerify(){
        alertDialog = MaterialAlertDialogBuilder(this.requireContext())
            .setView(ProgressBar(this.requireContext()))
            .setMessage("Loading...")
            .show()
        alertDialog.setCanceledOnTouchOutside(false)

        viewModel.signUpCode(binding.codeEditText.text.toString())
    }

    private fun signUpCodeLogic(state: SlackState<Any?>){
        when (state.status){
            "0" -> {
                null
            }
            else -> {
                alertDialog.dismiss()
                when(state.status) {
                    "200" -> {
                        Toast.makeText(context, "회원가입이 모두 완료되었습니다!!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(SignUpCodeFragmentDirections.actionGlobalAuthCheckFragment())
                    }
                    "-2" -> {
                        Toast.makeText(context, "코드를 다시 입력해주세요...", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.resetSignUpCodeState()
            }
        }
    }
}