package com.waffle22.wafflytime.ui.boards.boardscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        binding.posts.adapter = postPreviewAdapter
        binding.posts.layoutManager = LinearLayoutManager(this.context)

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
            viewModel.refreshBoard(boardId, boardType)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.posts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤이 끝에 도달했는지 확인
                if (binding.posts.canScrollVertically(1)) {
                    Log.d("BoardFragemt", "end of scroll")
                    viewModel.getPosts(boardId, boardType)
                }
            }
        })
    }

    override fun onStop(){
        super.onStop()
        viewModel.reset()
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