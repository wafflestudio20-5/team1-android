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
            btnSignup.setOnClickListener { signUp() }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.signUpState.collect {
                signUpLogic(it)
            }
        }
    }

    private fun signUp(){
        viewModel.signUp(binding.idEditText.text.toString(), binding.passwordEditText.text.toString())

        alertDialog = MaterialAlertDialogBuilder(this.requireContext())
            .setView(ProgressBar(this.requireContext()))
            .setMessage("Loading...")
            .show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun signUpLogic(status: SignUpStatus){
        when (status){
            SignUpStatus.StandBy -> {
                null
            }
            else -> {
                alertDialog.dismiss()
                when(status) {
                    SignUpStatus.SignUpOk -> {
                        findNavController().navigate(SignUpFragmentDirections.actionGlobalMainHomeFragment())
                    }
                    SignUpStatus.SignUpConflict -> {
                        Toast.makeText(context, "이미 존재하는 아이디입니다", Toast.LENGTH_SHORT).show()
                    }
                    SignUpStatus.Error_500 -> {
                        Toast.makeText(context, "500 Error", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Unknown Error", Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.resetSignUpState()
            }
        }
    }
}