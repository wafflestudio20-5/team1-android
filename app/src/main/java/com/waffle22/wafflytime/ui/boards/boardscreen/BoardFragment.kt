package com.waffle22.wafflytime.ui.boards.boardscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffle22.wafflytime.databinding.FragmentBoardBinding
import com.waffle22.wafflytime.network.dto.BoardType
import com.waffle22.wafflytime.network.dto.TimeDTO
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BoardFragment() : Fragment() {
    private lateinit var binding: FragmentBoardBinding

    private val viewModel: BoardViewModel by sharedViewModel()

    private val navigationArgs: BoardFragmentArgs by navArgs()

    private var boardId = 0L
    private lateinit var boardType: BoardType

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBoardBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postPreviewAdapter = PostPreviewAdapter{
            val action = BoardFragmentDirections.actionBoardFragmentToPostFragment(it.boardId, it.postId)
            this.findNavController().navigate(action)
        }
        viewModel.posts.observe(this.viewLifecycleOwner){ items ->
            items.let{
                postPreviewAdapter.submitList(it)
            }
        }
        binding.threads.adapter = postPreviewAdapter
        binding.threads.layoutManager = LinearLayoutManager(this.context)

        val boardAnnouncementAdapter = BoardAnnouncementAdapter{
            val action = BoardFragmentDirections.actionBoardFragmentToPostFragment(it.boardId, it.postId)
            this.findNavController().navigate(action)
        }
        viewModel.announcements.observe(this.viewLifecycleOwner){ items->
            items.let{
                boardAnnouncementAdapter.submitList(it)
            }
        }
        binding.announcements.adapter = boardAnnouncementAdapter
        binding.announcements.layoutManager = LinearLayoutManager(this.context)

        boardId = navigationArgs.boardId
        boardType = navigationArgs.boardType
        viewModel.getBoardInfo(boardId, boardType)
        viewModel.getPosts(boardId, boardType)

        lifecycleScope.launchWhenStarted {
            viewModel.postsLoadingState.collect{
                showPostsLogic(it)
            }
        }

        if(boardType == BoardType.Common){
            binding.newThread.setOnClickListener{
                val action = BoardFragmentDirections.actionBoardFragmentToNewPostFragment(boardId)
                this.findNavController().navigate(action)
            }
        }
        else    binding.newThread.visibility = View.GONE


        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            if((binding.announcements.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() == 0)
                viewModel.refreshBoard(boardId, boardType)
            else if((binding.threads.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == viewModel.posts.value!!.size)
                viewModel.getPosts(boardId, boardType)
            binding.swipeRefreshLayout.isRefreshing = false
        }

    }

    private fun showPostsLogic(status: PostsLoadingStatus){
        when (status) {
            PostsLoadingStatus.Success ->{
                Log.v("BoardFragment", "Posts Loading Success")
                binding.toolbar.title = viewModel.boardInfo.value!!.title
                //binding.toolbar.title = viewModel.boardInfo.value!!.title + "\n" + viewModel.boardInfo.value!!.description
            }
            PostsLoadingStatus.TokenExpired -> {
                viewModel.getPosts(boardId,boardType)
            }
            else -> {
                Log.v("BoardFragment", "Error occurred")
            }
        }
    }
}