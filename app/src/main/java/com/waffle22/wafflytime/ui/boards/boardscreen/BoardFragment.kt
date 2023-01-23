package com.waffle22.wafflytime.ui.boards.boardscreen

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentBoardBinding
import com.waffle22.wafflytime.network.dto.BoardType
import com.waffle22.wafflytime.network.dto.LoadingStatus
import com.waffle22.wafflytime.network.dto.PostTaskType
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BoardFragment : Fragment() {
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
        setupMenu()

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

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postsLoadingState.collect{
                    showPostsLogic(it)
                }
            }
        }

        if(boardType == BoardType.Common){
            binding.newThread.setOnClickListener{
                val action = BoardFragmentDirections.actionBoardFragmentToNewPostFragment(boardId, PostTaskType.CREATE)
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
                    Log.d("BoardFragment", "end of scroll")
                    viewModel.getPosts(boardId, boardType)
                }
            }
        })
    }

    override fun onStop(){
        super.onStop()
        viewModel.reset()
    }

    private fun setupMenu(){
        binding.toolbar.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                //Log.d("BoardFragment", "onCreateMenu")
                menuInflater.inflate(R.menu.board_actions, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                //Log.d("BoardFragment", "OnMenuItemSelected")
                when (menuItem.itemId) {
                    R.id.action_search -> {
                        val action = BoardFragmentDirections.actionBoardFragmentToSearchPostFragment()
                        findNavController().navigate(action)
                    }
                    R.id.refresh -> viewModel.refreshBoard(boardId, boardType)
                    R.id.write -> {
                        val action = BoardFragmentDirections.actionBoardFragmentToNewPostFragment(boardId, PostTaskType.CREATE)
                        findNavController().navigate(action)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showPostsLogic(status: LoadingStatus){
        when (status) {
            LoadingStatus.Standby -> Toast.makeText(context, "로딩중", Toast.LENGTH_SHORT).show()
            LoadingStatus.Success ->{
                Log.v("BoardFragment", "Posts Loading Success")
                binding.toolbar.title = viewModel.boardInfo.value!!.title
                binding.description.text = viewModel.boardInfo.value!!.description
                //binding.toolbar.title = viewModel.boardInfo.value!!.title + "\n" + viewModel.boardInfo.value!!.description
            }
            LoadingStatus.Error -> {
                Toast.makeText(context, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
                Log.v("BoardFragment", "Error occurred")
            }
            LoadingStatus.Corruption -> Toast.makeText(context, "알 수 없는 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
        }
    }
}