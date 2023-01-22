package com.waffle22.wafflytime.ui.boards.newpost

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.waffle22.wafflytime.databinding.FragmentNewThreadBinding
import com.waffle22.wafflytime.network.dto.LoadingStatus
import com.waffle22.wafflytime.network.dto.PostTaskType
import com.waffle22.wafflytime.ui.boards.postscreen.PostViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewPostFragment : Fragment() {
    private lateinit var binding: FragmentNewThreadBinding
    private val viewModel: NewPostViewModel by sharedViewModel()
    private val postViewModel: PostViewModel by sharedViewModel()
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

        if (navigationArgs.taskType == PostTaskType.EDIT){
            binding.title.setText(postViewModel.curPost.value!!.title?:"")
            binding.contents.setText(postViewModel.curPost.value!!.contents)
        }

        viewModel.getBoardInfo(navigationArgs.boardId)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.boardLoadingStatus.collect {
                    bindSubmitButton(it)
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createPostStatus.collect{
                    submitPostLogic(it)
                }
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
                binding.title.visibility = if(viewModel.boardInfo.value!!.boardType == "DEFAULT") View.VISIBLE else View.GONE
                binding.submitButton.setOnClickListener{
                    if(navigationArgs.taskType == PostTaskType.CREATE)
                        viewModel.createNewPost(if(viewModel.boardInfo.value!!.boardType == "DEFAULT") binding.title.text.toString() else null,
                        binding.contents.text.toString(), binding.isQuestion.isChecked, binding.isAnonymous.isChecked)
                    else
                        viewModel.editPost(postViewModel.curPost.value!!,
                            if(viewModel.boardInfo.value!!.boardType == "DEFAULT") binding.title.text.toString() else null,
                            binding.contents.text.toString())
                }
            }
            else -> {
                Log.v("NewPostFragment", "Board Loading Error")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.title.setText("")
        binding.contents.setText("")
        viewModel.resetStates()
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