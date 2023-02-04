package com.waffle22.wafflytime.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.waffle22.wafflytime.databinding.FragmentLoginBinding
import com.waffle22.wafflytime.databinding.FragmentNicknameBinding
import com.waffle22.wafflytime.util.SlackState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NickNameFragment: Fragment() {
    private lateinit var binding: FragmentNicknameBinding
    private var alertDialog: AlertDialog? = null
    private val loginViewModel: LoginViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNicknameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnSignup.setOnClickListener { loginViewModel.socialSignUp(binding.nickNameText.text.toString()) }
        }

        lifecycleScope.launch {
            loginViewModel.loginState.collect {
                nickNameLogic(it)
            }
        }
    }

    private fun nickNameLogic(status: SlackState<Nothing>) {
        when (status.status) {
            "0" -> {
                null
            }
            else -> {
                alertDialog?.dismiss()
                when (status.status) {
                    "200" -> {
                        findNavController().navigate(LoginFragmentDirections.actionGlobalAuthCheckFragment())
                    }
                    else -> {
                        Toast.makeText(context, status.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                loginViewModel.resetAuthState()
            }
        }
    }
}