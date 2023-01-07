package com.waffle22.wafflytime.ui.login


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentLoginBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment :  Fragment() {
    private lateinit var binding: FragmentLoginBinding
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
        }

        lifecycleScope.launchWhenStarted {
            viewModel.authState.collect {
                when (it){
                    "StandBy" -> {
                        null
                    }
                    "LoginOk" -> {
                        // TODO() 성공했다는 메세지 띄워주자 토스트메세지로
                        // 뒤로가기 누르면 로그인페이지로 다시 못오게 해야됨
                        findNavController().navigate(R.id.action_loginFragment_to_baseNotificationFragment)
                    }
                    "LoginFailed" -> {
                        Log.d("debug","login LoginFailed")
                    } // TODO: Generate ToastMessage (Login Failed, Check Id or Password)
                    else -> {
                        Log.d("debug","login unexpected error")
                    } // TODO: Generate ToastMessage (Unknown Error Occurred)
                }
                if (it != "StandBy"){
                    viewModel.resetAuthState()
                }
            }
        }

    }

    private fun login(){
        // TODO: 이거 눌렸을때 다른버튼 안눌리게 하기, 그러니까 로그인버튼과 회원가입버튼이 동시에 눌리면? 이런거 방지
        viewModel.login(binding.idEditText.text.toString(), binding.passwordEditText.text.toString())
    }
    private fun signUp(){
        findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
    }
}