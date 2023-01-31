package com.waffle22.wafflytime.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.waffle22.wafflytime.databinding.FragmentAuthcheckBinding
import com.waffle22.wafflytime.util.SlackState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AuthCheckFragment: Fragment() {
    private lateinit var binding: FragmentAuthcheckBinding
    private val viewModel: AuthCheckViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthcheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 1. getUserInfo 이용해서 인증하기
        viewModel.checkAuth()
        
        // 2. 뭐 받으면 로직 실행
        lifecycleScope.launchWhenStarted {
            viewModel.authState.collect {
                authLogic(it)
            }
        }

    }

    private fun authLogic(state: SlackState<Any?>){
        when (state.status){
            "0" -> {
                null
            }
            else -> {
                when(state.status) {
                    "200" -> {
                        findNavController().navigate(AuthCheckFragmentDirections.actionAuthCheckFragmentToMainHomeFragment())
                    }
                    else -> {
                        Toast.makeText(context, "인증에 실패했습니다 (${state.errorMessage})", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(AuthCheckFragmentDirections.actionGlobalLoginFragment())
                    }
                }
                viewModel.resetAuthState()
            }
        }
    }
}