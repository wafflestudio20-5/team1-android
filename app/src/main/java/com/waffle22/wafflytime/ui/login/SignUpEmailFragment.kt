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
import com.waffle22.wafflytime.databinding.FragmentSignupBinding
import com.waffle22.wafflytime.databinding.FragmentSignupEmailBinding
import com.waffle22.wafflytime.util.StateStorage
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignUpEmailFragment: Fragment() {
    private lateinit var binding: FragmentSignupEmailBinding
    private lateinit var alertDialog: AlertDialog
    private val viewModel: SignUpEmailViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply{
            btnSendEmailCode.setOnClickListener { emailVerify() }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.signUpEmailState.collect {
                signUpEmailLogic(it)
            }
        }
    }

    private fun emailVerify(){
        viewModel.signUpEmail(binding.emailEditText.text.toString())

        alertDialog = MaterialAlertDialogBuilder(this.requireContext())
            .setView(ProgressBar(this.requireContext()))
            .setMessage("Loading...")
            .show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun signUpEmailLogic(state: StateStorage){
        when (state.status){
            "0" -> {
                null
            }
            else -> {
                alertDialog.dismiss()
                when(state.status) {
                    "200" -> {
                        findNavController().navigate(SignUpEmailFragmentDirections.actionSignUpEmailFragmentToSignUpCodeFragment())
                    }
                    else -> {
                        Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.resetSignUpEmailState()
            }
        }
    }
}