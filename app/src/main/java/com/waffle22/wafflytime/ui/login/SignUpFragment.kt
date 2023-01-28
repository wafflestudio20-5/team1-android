package com.waffle22.wafflytime.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentSignupBinding
import com.waffle22.wafflytime.util.SlackState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
    private lateinit var alertDialog: AlertDialog
    private val viewModel: SignUpViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply{
            btnSignUpDone.setOnClickListener { signUp() }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.signUpState.collect {
                signUpLogic(it)
            }
        }
    }



    private fun signUp(){
        alertDialog = MaterialAlertDialogBuilder(this.requireContext())
            .setView(ProgressBar(this.requireContext()))
            .setMessage("Loading...")
            .show()
        alertDialog.setCanceledOnTouchOutside(false)

        viewModel.signUp(binding.idEditText.text.toString(), binding.passwordEditText.text.toString(), binding.nickNameEditText.text.toString())
    }

    private fun signUpLogic(state: SlackState<Nothing>){
        when (state.status){
            "0" -> {
                null
            }
            else -> {
                alertDialog.dismiss()
                when(state.status) {
                    "200" -> {
                        dialogForEmail()
                    }
                    else -> {
                        Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.resetSignUpState()
            }
        }
    }

    private fun dialogForEmail() {
        alertDialog = MaterialAlertDialogBuilder(this.requireContext())
            .setMessage("이메일 인증을 하시겠습니까?")
            .setPositiveButton("예") { dialog, which ->
                alertDialog.dismiss()
                findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignUpEmailFragment())
            }
            .setNegativeButton("아니요") { dialog, which ->
                alertDialog.dismiss()
                findNavController().navigate(SignUpFragmentDirections.actionGlobalAuthCheckFragment())
            }
            .show()
        alertDialog.setCanceledOnTouchOutside(false)
    }
}