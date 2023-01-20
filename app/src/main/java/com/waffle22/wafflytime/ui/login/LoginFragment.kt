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
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApi
import com.kakao.sdk.user.UserApiClient
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentLoginBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment :  Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var alertDialog: AlertDialog

    private val viewModel: LoginViewModel by sharedViewModel()

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

        lifecycleScope.launchWhenStarted {
            viewModel.loginState.collect {
                loginLogic(it)
            }
        }

    }

    private fun login(){
        // TODO: 이거 눌렸을때 다른버튼 안눌리게 하기, 그러니까 로그인버튼과 회원가입버튼이 동시에 눌리면? 이런거 방지
        viewModel.login(binding.idEditText.text.toString(), binding.passwordEditText.text.toString())

        alertDialog =MaterialAlertDialogBuilder(this.requireContext())
            .setView(ProgressBar(this.requireContext()))
            .setMessage("Loading...")
            .show()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun loginLogic(status: LoginStatus){
        when (status){
            LoginStatus.StandBy -> {
                null
            }
            else -> {
                alertDialog.dismiss()
                when(status) {
                    LoginStatus.LoginOk -> {
                        findNavController().navigate(LoginFragmentDirections.actionGlobalMainHomeFragment())
                    }
                    LoginStatus.LoginFailed -> {
                        Toast.makeText(
                            context,
                            "로그인 실패(id, password 다시 확인)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    LoginStatus.Error_500 -> {
                        Toast.makeText(context, "500 Error", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Unknown Error", Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.resetAuthState()
            }
        }
    }

    private fun signUp(){
        findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
    }

}