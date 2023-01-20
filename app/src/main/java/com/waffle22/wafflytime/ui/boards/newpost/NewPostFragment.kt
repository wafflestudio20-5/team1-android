package com.waffle22.wafflytime.ui.boards.newpost

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.waffle22.wafflytime.databinding.FragmentNewThreadBinding
import com.waffle22.wafflytime.network.dto.LoadingStatus
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewPostFragment : Fragment() {
    private lateinit var binding: FragmentNewThreadBinding
    private val viewModel: NewPostViewModel by sharedViewModel()
    private val navigationArgs: NewPostFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewThreadBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getBoardInfo(navigationArgs.boardId)
        lifecycleScope.launchWhenStarted {
            viewModel.boardLoadingStatus.collect{
                bindSubmitButton(it)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.createPostStatus.collect{
                submitPostLogic(it)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    fun bindSubmitButton(status: LoadingStatus){
        when (status){
            LoadingStatus.Standby -> null
            LoadingStatus.Success -> {
                binding.submitButton.setOnClickListener{
                    viewModel.createNewPost(binding.title.text.toString(), binding.contents.text.toString(), binding.isQuestion.isChecked, binding.isAnonymous.isChecked)
                }
            }
            else -> {
                Log.v("NewPostFragment", "Board Loading Error")
            }
        }
    }

    fun submitPostLogic(status: LoadingStatus){
        when(status){
            LoadingStatus.Standby -> null
            LoadingStatus.Success -> {
                findNavController().navigateUp()
            }
            else -> {
                Log.v("NewPostFragment", "Submit Post Error")
            }
        }
    }
}