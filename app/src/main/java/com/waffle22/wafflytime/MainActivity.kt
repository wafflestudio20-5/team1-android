package com.waffle22.wafflytime

import android.os.Bundle

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.kakao.sdk.common.KakaoSdk.keyHash
import com.kakao.sdk.common.util.Utility
import com.waffle22.wafflytime.databinding.ActivityMainBinding
import kotlin.reflect.KParameter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("Debug", Utility.getKeyHash(this))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.mainHomeFragment -> showBottomNav()
                R.id.baseNotificationFragment -> showBottomNav()
                R.id.boardListFragment -> showBottomNav()
                else -> hideBottomNav()
            }

        }
    }

    private fun hideBottomNav() {
        binding.bottomNav.visibility = View.GONE
    }
    private fun showBottomNav() {
        binding.bottomNav.visibility = View.VISIBLE
    }
}