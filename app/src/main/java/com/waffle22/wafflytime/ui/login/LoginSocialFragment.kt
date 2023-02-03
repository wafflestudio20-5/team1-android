package com.waffle22.wafflytime.ui.login


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentLoginBinding
import com.waffle22.wafflytime.databinding.FragmentLoginSocialBinding
import com.waffle22.wafflytime.databinding.FragmentSignupSocialBinding
import com.waffle22.wafflytime.util.SlackState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class LoginSocialFragment : Fragment() {
    private lateinit var binding: FragmentLoginSocialBinding
    private lateinit var alertDialog: AlertDialog

    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginSocialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.loginState.collect {
                loginLogic(it)
            }
        }
        val webview = WebView()

        val args: LoginSocialFragmentArgs by navArgs() //Args 만든 후
        var path = args.path // 아까 만든 msg 를 tMsg에 대입
        when(path) {
            "kakao"->kakaoLogin()
            "naver"->naverLogin()
            "google"->googleLogin()
            else ->githubLogin()
        }

    }


    private fun kakaoLogin() {
        alertDialog = MaterialAlertDialogBuilder(this.requireContext())
            .setView(ProgressBar(this.requireContext()))
            .setMessage("Loading...")
            .show()
        alertDialog.setCanceledOnTouchOutside(false)

        val CLIENT_ID = "2e73508a53ba1108841a05a1612720fd"
        val REDIRECT_URI = "http://localhost:3000/oauth/kakao/callback"
        val KAKAO_AUTH_URL =
            "https://kauth.kakao.com/oauth/authorize?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code"

        /*
        val intent =

            Intent(Intent.ACTION_VIEW, Uri.parse(KAKAO_AUTH_URL))
        registerForActivityResult(intent)
        */
        viewModel.socialLogin("kakao", KAKAO_AUTH_URL)
    }

    private fun naverLogin() {
        alertDialog = MaterialAlertDialogBuilder(this.requireContext())
            .setView(ProgressBar(this.requireContext()))
            .setMessage("Loading...")
            .show()
        alertDialog.setCanceledOnTouchOutside(false)

        val CLIENT_ID ="83lZcr9dJCEsE6H18g_Z"
        val REDIRECT_URI = "http://localhost:3000/oauth/naver/callback"
        val NAVER_AUTH_URL = ""

        viewModel.socialLogin("naver", NAVER_AUTH_URL)
    }

    private fun googleLogin() {
        alertDialog = MaterialAlertDialogBuilder(this.requireContext())
            .setView(ProgressBar(this.requireContext()))
            .setMessage("Loading...")
            .show()
        alertDialog.setCanceledOnTouchOutside(false)

        val CLIENT_ID ="http://586425104922-9d6vrvjkncq158aeu0gon6ipobln6jkj.apps.googleusercontent.com"
        val REDIRECT_URI = "http://localhost:3000/oauth/google/callback"
        val GOOGLE_AUTH_URL = ""

        viewModel.socialLogin("google", GOOGLE_AUTH_URL)
    }

    private fun githubLogin() {
        alertDialog = MaterialAlertDialogBuilder(this.requireContext())
            .setView(ProgressBar(this.requireContext()))
            .setMessage("Loading...")
            .show()
        alertDialog.setCanceledOnTouchOutside(false)

        val CLIENT_ID ="86fc35fdaf29999dcded"
        val REDIRECT_URI = "http://localhost:3000/oauth/github/callback"
        val GITHUB_AUTH_URL = ""
        viewModel.socialLogin("github", GITHUB_AUTH_URL)
    }

    private fun loginLogic(status: SlackState<Nothing>) {
        when (status.status) {
            "0" -> {
                null
            }
            else -> {
                alertDialog.dismiss()
                when (status.status) {
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

    private fun socialSignUp() {
        findNavController().navigate(R.id.action_loginSocialFragment_to_signUpSocialFragment)
    }
}
