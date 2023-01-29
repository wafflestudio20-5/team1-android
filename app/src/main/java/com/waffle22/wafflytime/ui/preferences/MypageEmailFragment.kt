package com.waffle22.wafflytime.ui.preferences

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
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentMypageEmailBinding
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MypageEmailFragment: Fragment() {
    lateinit var binding: FragmentMypageEmailBinding
    private lateinit var alertDialog: AlertDialog
    private val viewModel: MypageEmailViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.emailSendButton.setOnClickListener {
            submitEmail()
        }
    }

    override fun onStart() {
        super.onStart()
        if(viewModel.isVerified()) {
            Toast.makeText(requireContext(), "이미 인증된 계정입니다.", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    fun submitEmail() {
        val inputEmail = binding.inputEmail.editText!!.text.toString()
        if(inputEmail.isNotEmpty()) {
            viewModel.submitEmail(inputEmail)
            alertDialog = MaterialAlertDialogBuilder(this.requireContext())
                .setView(ProgressBar(this.requireContext()))
                .setMessage("Loading...")
                .show()
            alertDialog.setCanceledOnTouchOutside(false)
            lifecycleScope.launch {
                viewModel.emailState.collect {
                    if(it.status != "0") {
                        alertDialog.dismiss()
                        if (it.errorCode == null && it.errorMessage == null) {
                            findNavController().navigate(R.id.action_mypageEmailFragment_to_mypageEmailCodeFragment)
                        } else {
                            Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                        coroutineContext.job.cancel()
                    }
                }
            }
        }

    }
}