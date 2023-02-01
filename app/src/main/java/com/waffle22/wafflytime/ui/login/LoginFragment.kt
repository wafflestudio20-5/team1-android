package com.waffle22.wafflytime.ui.login


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.content.Intent
import android.net.Uri
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
import com.waffle22.wafflytime.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch
import com.waffle22.wafflytime.util.SlackState
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
            kakaoLoginButton.setOnClickListener{
                val CLIENT_ID = "14e86042a3842d295c4ef5af422fac3d"
                val REDIRECT_URI =  "http://localhost:3000/api/auth/social/login/kakao"
                val KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/authorize?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code"
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse(KAKAO_AUTH_URL))
                startActivity(intent)

            }
            naverLoginButton.setOnClickListener{ naverLogin()}
            googleLoginButton.setOnClickListener{ googleLogin()}
            githubLoginButton.setOnClickListener{ githubLogin()}
        }

        lifecycleScope.launch {
            viewModel.loginState.collect {
                loginLogic(it)
            }
        }

    }

    private fun login(){
        alertDialog =MaterialAlertDialogBuilder(this.requireContext())
            .setView(ProgressBar(this.requireContext()))
            .setMessage("Loading...")
            .show()
        alertDialog.setCanceledOnTouchOutside(false)

        viewModel.login(binding.idEditText.text.toString(), binding.passwordEditText.text.toString())

    }

    /*
    private fun kakaoLogin() {

        viewModel.kakaoSocialLogin(requireContext())
    }
*/

    private fun naverLogin() {
        viewModel.naverSocialLogin()
    }

    private fun googleLogin() {
        viewModel.googleSocialLogin()
    }

    private fun githubLogin() {
        viewModel.githubSocialLogin()
    }

    private fun loginLogic(status: SlackState<Nothing>){
        when (status.status){
            "0" -> {
                null
            }
            else -> {
                alertDialog.dismiss()
                when(status.status) {
                    "200" -> {
                        findNavController().navigate(LoginFragmentDirections.actionGlobalAuthCheckFragment())
                    }
                    else -> {
                        Toast.makeText(context, status.errorMessage, Toast.LENGTH_SHORT).show()
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
