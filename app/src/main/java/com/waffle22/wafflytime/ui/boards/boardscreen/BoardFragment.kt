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
import com.waffle22.wafflytime.util.SlackState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BoardFragment : Fragment() {
    private lateinit var binding: FragmentBoardBinding
    private lateinit var postPreviewAdapter: PostPreviewAdapter

    private val viewModel: BoardViewModel by sharedViewModel()
    private val navigationArgs: BoardFragmentArgs by navArgs()

    private var boardId = 0L
    private lateinit var boardType: BoardType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        boardId = navigationArgs.boardId
        boardType = navigationArgs.boardType
        viewModel.refreshBoard(boardId, boardType)
    }

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

        postPreviewAdapter = PostPreviewAdapter{
            val action = BoardFragmentDirections.actionBoardFragmentToPostFragment(it.boardId, it.postId)
            this.findNavController().navigate(action)
        }
        lifecycleScope.launch {
            viewModel.boardScreenState.collect {
                boardScreenLogic(it)
            }
        }

        binding.apply {
            posts.adapter = postPreviewAdapter
            posts.layoutManager = LinearLayoutManager(context)

            if(boardType == BoardType.Common){
                newThread.setOnClickListener{
                    val action = BoardFragmentDirections.actionBoardFragmentToNewPostFragment(boardId, PostTaskType.CREATE)
                    findNavController().navigate(action)
                }
            } else {
                newThread.visibility = View.GONE
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            swipeRefreshLayout.setOnRefreshListener {
                viewModel.refreshBoard(boardId, boardType)
                swipeRefreshLayout.isRefreshing = false
            }

            posts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    // 스크롤이 끝에 도달했는지 확인
                    if (!posts.canScrollVertically(1)) {
                        // TODO: 커서기반 페이지네이션 나오면 그때 구현하기 고고
                        //viewModel.getPosts(boardId, boardType)
                    }
                }
            })
        }
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

    private fun boardScreenLogic(state: SlackState<BoardDataHolder>) {
        when (state.status) {
            "0" -> null
            else -> {
                when (state.status) {
                    "200" -> {
                        val data = state.dataHolder!!
                        binding.toolbar.title = data.boardInfo!!.title
                        binding.description.text = data.boardInfo!!.description
                        postPreviewAdapter.submitList(data.boardData)
                    }
                    else -> {
                        Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.resetState()
            }
        }
    }
}