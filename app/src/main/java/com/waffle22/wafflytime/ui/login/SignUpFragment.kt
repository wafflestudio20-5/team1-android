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
import com.waffle22.wafflytime.databinding.FragmentSignupBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
    private val viewModel: SignUpViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply{
            btnSignup.setOnClickListener { signUp() }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.signUpState.collect {
                when (it){
                    "StandBy" -> {
                        null
                    }
                    "SignUpOk" -> {
                        // TODO: 회원가입 성공했다는 메세지를 토스트메세지로 띄워줄것
                        // TODO: 뒤로가기 누르면 로그인페이지로 다시 못오게 해야됨
                        findNavController().navigate(R.id.action_signUpFragment_to_baseNotificationFragment)
                    }
                    "SignUpConflict" -> {
                        Log.d("debug"," signup Failed by conflict")
                    } // TODO: Generate ToastMessage (Login Failed, Check Id or Password)
                    else -> {
                        Log.d("debug","unexpected error of signup")
                    } // TODO: Generate ToastMessage (Unknown Error Occurred)
                }
                if (it != "StandBy"){
                    viewModel.resetSignUpState()
                }
            }
        }

    }

    private fun signUp(){
        // TODO: 이거 눌렸을때 다른버튼 안눌리게 하기, 한번 더누르거나 그런거 방지
        viewModel.signUp(binding.idEditText.text.toString(), binding.passwordEditText.text.toString())
    }
}