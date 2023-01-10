package com.waffle22.wafflytime.ui.login


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
                        findNavController().navigate(LoginFragmentDirections.actionGlobalMainHomeFragment())
                    }
                    "LoginFailed" -> {
                        Toast.makeText(context, "로그인 실패(id, password 다시 확인)", Toast.LENGTH_SHORT).show()
                    }
                    "500Error"-> {
                        Toast.makeText(context, "500 Error", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Unknown Error", Toast.LENGTH_SHORT).show()
                    }
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