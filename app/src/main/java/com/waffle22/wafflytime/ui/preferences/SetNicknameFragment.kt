package com.waffle22.wafflytime.ui.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.waffle22.wafflytime.databinding.FragmentSetNicknameBinding
import com.waffle22.wafflytime.util.AuthStorage
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetNicknameFragment: Fragment() {

    private lateinit var binding: FragmentSetNicknameBinding
    private val viewModel: SetNicknameViewModel by viewModel()
    private lateinit var alertDialog: AlertDialog
    private val authStorage: AuthStorage by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetNicknameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nicknameInput.editText!!.setText(authStorage.authInfo.value!!.userInfo!!.nickname)
        binding.submitButton.setOnClickListener {
            if(binding.nicknameInput.editText!!.text.toString().isNotEmpty()) {
                viewModel.changeUsername(binding.nicknameInput.editText!!.text.toString())
                alertDialog = MaterialAlertDialogBuilder(requireContext())
                    .setView(ProgressBar(requireContext()))
                    .setMessage("Loading...")
                    .show()
                alertDialog.setCanceledOnTouchOutside(false)
                viewModel.viewModelScope.launch {
                    viewModel.state.collect {
                        if(it.status != "0") {
                            if(it.errorCode == null && it.errorMessage == null) {
                                alertDialog.dismiss()
                                Toast.makeText(requireContext(), "닉네임이 변경되었습니다", Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            }
                            else {
                                alertDialog.dismiss()
                                Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}