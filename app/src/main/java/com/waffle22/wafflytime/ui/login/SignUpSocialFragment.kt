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
import com.waffle22.wafflytime.databinding.FragmentSignupSocialBinding
import com.waffle22.wafflytime.util.SlackState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignUpSocialFragment : Fragment() {
    private lateinit var binding: FragmentSignupSocialBinding
    private val viewModel: SignUpViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupSocialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnSignUpDone.setOnClickListener { }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.signUpState.collect {
                //signUpLogic(it)
            }
        }
    }
}