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
                        findNavController().navigate(SignUpFragmentDirections.actionGlobalMainHomeFragment())
                    }
                    "SignUpConflict" -> {
                        Toast.makeText(context, "이미 존재하는 아이디입니다", Toast.LENGTH_SHORT).show()
                    }
                    "500Error"-> {
                        Toast.makeText(context, "500 Error", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Unknown Error", Toast.LENGTH_SHORT).show()
                    }
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