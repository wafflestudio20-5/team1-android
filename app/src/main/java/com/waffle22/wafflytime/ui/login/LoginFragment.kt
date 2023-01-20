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
<<<<<<< HEAD
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApi
import com.kakao.sdk.user.UserApiClient
=======
import com.google.android.material.dialog.MaterialAlertDialogBuilder
>>>>>>> 84f1f575a43063fb3394bb769aaf08e2515303ca
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentLoginBinding
import com.waffle22.wafflytime.util.StateStorage
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
        alertDialog =MaterialAlertDialogBuilder(this.requireContext())
            .setView(ProgressBar(this.requireContext()))
            .setMessage("Loading...")
            .show()
        alertDialog.setCanceledOnTouchOutside(false)

        viewModel.login(binding.idEditText.text.toString(), binding.passwordEditText.text.toString())
    }

<<<<<<< HEAD
=======
    private fun loginLogic(status: StateStorage){
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

>>>>>>> 84f1f575a43063fb3394bb769aaf08e2515303ca
    private fun signUp(){
        findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
    }

}