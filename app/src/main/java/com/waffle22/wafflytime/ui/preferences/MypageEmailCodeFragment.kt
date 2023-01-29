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
import com.waffle22.wafflytime.databinding.FragmentMypageEmailCodeBinding
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MypageEmailCodeFragment: Fragment() {

    private lateinit var binding: FragmentMypageEmailCodeBinding
    private val viewModel: MypageEmailViewModel by sharedViewModel()
    private lateinit var alertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageEmailCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitCodeButton.setOnClickListener {
            checkCode()
        }
    }

    fun checkCode() {
        val code = binding.inputCode.editText!!.text.toString()
        if(code.isNotEmpty()) {
            viewModel.checkCode(code)
            alertDialog = MaterialAlertDialogBuilder(this.requireContext())
                .setView(ProgressBar(this.requireContext()))
                .setMessage("Loading...")
                .show()
            alertDialog.setCanceledOnTouchOutside(false)
            lifecycleScope.launch {
                viewModel.codeState.collect {
                    if(it.status != "0") {
                        alertDialog.dismiss()
                        if(it.errorCode == null && it.errorMessage == null) {
                            Toast.makeText(requireContext(), "학교 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                        else {
                            Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_SHORT).show()
                        }
                        coroutineContext.job.cancel()
                    }
                }
            }
        }
    }
}