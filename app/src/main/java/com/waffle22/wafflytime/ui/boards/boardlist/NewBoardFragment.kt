package com.waffle22.wafflytime.ui.boards.boardlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentNewBoardBinding
import com.waffle22.wafflytime.network.dto.LoadingStatus
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewBoardFragment : Fragment() {
    private lateinit var binding: FragmentNewBoardBinding
    private val viewModel: BoardListViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.setText("")
        binding.description.setText("")

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.submitButton.setOnClickListener {
            viewModel.createBoard(
                binding.title.text.toString(),
                when(binding.boardType.checkedRadioButtonId){
                    R.id.custom_base -> "CUSTOM_BASE"
                    else -> "CUSTOM_PHOTO"
                },
                binding.description.text.toString(),
                binding.allowAnonymous.isChecked
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.createBoardState.collect{
                createBoardLogic(it)
            }
        }
    }

    private fun createBoardLogic(status: LoadingStatus){
        when(status) {
            LoadingStatus.Standby -> null
            LoadingStatus.Success -> {
                Log.v("CreateBoardFragment", "Board Creation Success")
                findNavController().navigateUp()
            }
            else -> {
                Log.v("BoardListFragment", "Board Creation Error")
            }
        }
    }
}