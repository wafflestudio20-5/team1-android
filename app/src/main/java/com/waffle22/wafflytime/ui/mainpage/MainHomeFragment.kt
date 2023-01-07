package com.waffle22.wafflytime.ui.login


import android.os.Bundle
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
import com.waffle22.wafflytime.ui.boards.boardlist.BoardListAdapter
import com.waffle22.wafflytime.ui.boards.boardlist.BoardListFragmentDirections

class MainHomeFragment :  Fragment() {
    private lateinit var binding: FragmentMainHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainHomeBinding.inflate(inflater, container, false)
        binding.buttonformore.setOnClickListener {
            val action = MainHomeFragmentDirections.actionMainHomeFragmentToBoardListFragment()
            findNavController().navigate(action)
        }
        return binding.root


    }

        /*
        val MainHomeListAdapter = BoardListAdapter {
            val action_2boardlist = MainHomeFragmentDirections.actionMainHomeFragmentToBoardListFragment()
            this.findNavController().navigate(action_2boardlist)
        }
        */
    }

