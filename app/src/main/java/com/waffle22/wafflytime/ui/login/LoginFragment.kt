package com.waffle22.wafflytime.ui.login


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApi
import com.kakao.sdk.user.UserApiClient
import com.waffle22.wafflytime.R
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
            btnLogin.setOnClickListener { login() }
            btnSignup.setOnClickListener { signUp() }
            kakaoLoginButton.setOnClickListener { viewModel.kakaoSocialLogin(requireContext())}
        }

    }

    private fun login(){
        viewModel.login(binding.idEditText.text.toString(), binding.passwordEditText.text.toString())
    }

    private fun signUp(){
        findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
    }

}