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
import com.waffle22.wafflytime.databinding.FragmentChangePasswordBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordFragment: Fragment() {

    private lateinit var binding: FragmentChangePasswordBinding
    private val viewModel: ChangePasswordViewModel by viewModel()
    private lateinit var alertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.changePasswordButton.setOnClickListener{
            changePassword()
        }
    }

    fun changePassword() {
        val newPassword = binding.newPasswordInput.editText!!.text.toString()
        val oldPassword = binding.currentPassword.editText!!.text.toString()
        val checkPassword = binding.newPasswordCheckInput.editText!!.text.toString()
        if(newPassword.isEmpty() || oldPassword.isEmpty() || checkPassword.isEmpty()) {
            return
        }
        viewModel.changePassword(newPassword, oldPassword, checkPassword)
        alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(ProgressBar(requireContext()))
            .setMessage("Loading...")
            .show()
        alertDialog.setCanceledOnTouchOutside(false)
        lifecycleScope.launch {
            viewModel.state.collect {
                if (it.status != "0") {
                    alertDialog.dismiss()
                    if(it.errorCode == null && it.errorMessage == null) {
                        Toast.makeText(requireContext(), "비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                    else {
                        Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}