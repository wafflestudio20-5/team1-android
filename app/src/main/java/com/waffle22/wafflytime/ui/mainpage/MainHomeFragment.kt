package com.waffle22.wafflytime.ui.login


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentLoginBinding
import com.waffle22.wafflytime.databinding.FragmentMainHomeBinding
import com.waffle22.wafflytime.ui.SettingsActivity
import com.waffle22.wafflytime.ui.boards.boardlist.BoardListAdapter
import com.waffle22.wafflytime.ui.boards.boardlist.BoardListFragmentDirections
import com.waffle22.wafflytime.ui.mainpage.MainHomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MainHomeFragment :  Fragment() {
    private lateinit var binding: FragmentMainHomeBinding
    private val viewModel: MainHomeViewModel by sharedViewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Login Verification
        if(!viewModel.isLogin()){
            this.findNavController().navigate(MainHomeFragmentDirections.actionGlobalLoginFragment())
        }

        binding.buttonSearch.setOnClickListener {
            viewModel.logOut()
            this.findNavController().navigate(MainHomeFragmentDirections.actionGlobalLoginFragment())
        }

        binding.buttonMyPage.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

    }

        /*
        val MainHomeListAdapter = BoardListAdapter {
            val action_2boardlist = MainHomeFragmentDirections.actionMainHomeFragmentToBoardListFragment()
            this.findNavController().navigate(action_2boardlist)
        }
        */
    }

