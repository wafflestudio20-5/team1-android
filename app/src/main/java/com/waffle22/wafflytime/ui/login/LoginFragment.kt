package com.waffle22.wafflytime.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.waffle22.wafflytime.databinding.FragmentLoginBinding

class LoginFragment :  Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply{
            //btnSignup.setOnClickListener {  }
            btnLogin.setOnClickListener { doLogin() }
        }

    }

    private fun doLogin(){
        viewModel.login(binding.idEditText.text.toString(), binding.passwordEditText.text.toString())
    }
}