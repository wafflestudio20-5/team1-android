package com.waffle22.wafflytime.ui.login

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.waffle22.wafflytime.databinding.FragmentWebviewBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WebViewFragment: Fragment() {
    private lateinit var binding: FragmentWebviewBinding
    private lateinit var navController: NavController
    private val loginViewModel: LoginViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        binding = FragmentWebviewBinding.inflate(inflater,container,false)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.settings.setSupportMultipleWindows(true)
        binding.webView.webViewClient = object: WebViewClient(){
            val target = "http://wafflytime.com/oauth/kakao/callback?code="
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("debug",url!!)
                if (url!!.substring(target.indices) == target) {
                    val code = url!!.substring(target.length)
                    Log.d("debug",code)
                    loginViewModel.socialLogin("kakao",code)
                    navController.navigateUp()
                }
            }
        }

        val CLIENT_ID = "2e73508a53ba1108841a05a1612720fd"
        val REDIRECT_URI = "http://wafflytime.com/oauth/kakao/callback"
        val kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code"
        binding.webView.loadUrl(kakaoAuthUrl)
        return binding.root
    }
}