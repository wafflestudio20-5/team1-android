package com.waffle22.wafflytime.ui.boards.boardlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffle22.wafflytime.databinding.FragmentBoardListBinding
import com.waffle22.wafflytime.network.dto.BoardType
import com.waffle22.wafflytime.network.dto.LoadingStatus
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BoardListFragment : Fragment() {
    private lateinit var binding: FragmentBoardListBinding

    private val viewModel: BoardListViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoardListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val basicBoardListAdapter = BoardListAdapter{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(it.boardId, BoardType.Common)
            this.findNavController().navigate(action)
        }
        viewModel.basicBoards.observe(this.viewLifecycleOwner){items->
            items.let{
                basicBoardListAdapter.submitList(it)
            }
        }
        binding.defaultBoards.adapter = basicBoardListAdapter
        binding.defaultBoards.layoutManager = LinearLayoutManager(this.context)

        val customBoardListAdapter = BoardListAdapter{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(it.boardId, BoardType.Common)
            this.findNavController().navigate(action)
        }
        viewModel.customBoards.observe(this.viewLifecycleOwner){ items ->
            items.let{
                customBoardListAdapter.submitList(it)
            }
        }
        binding.allBoards.adapter = customBoardListAdapter
        binding.allBoards.layoutManager = LinearLayoutManager(this.context)

        val taggedBoardsAdapter = TaggedBoardsAdapter(this)
        viewModel.taggedBoards.observe(this.viewLifecycleOwner){ items ->
            items.let{
                taggedBoardsAdapter.submitList(it)
            }
        }
        binding.taggedBoards.adapter = taggedBoardsAdapter
        binding.taggedBoards.layoutManager = LinearLayoutManager(this.context)

        viewModel.getAllBoards()

        lifecycleScope.launchWhenStarted {
            viewModel.boardLoadingState.collect{
                showBoardsLogic(it)
            }
        }

        binding.myPosts.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(0, BoardType.MyPosts)
            this.findNavController().navigate(action)
        }
        binding.myComments.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(0, BoardType.MyReplies)
            this.findNavController().navigate(action)
        }
        binding.myScraps.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(0, BoardType.Scraps)
            this.findNavController().navigate(action)
        }
        binding.hotBoard.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(0, BoardType.Hot)
            this.findNavController().navigate(action)
        }
        binding.bestBoard.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(0, BoardType.Best)
            this.findNavController().navigate(action)
        }

        binding.search.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardSearchFragment()
            this.findNavController().navigate(action)
        }
    }

    private fun showBoardsLogic(status: LoadingStatus){
        when (status){
            LoadingStatus.Success -> {
                Log.v("BoardListFragment", "Board Loading Success")
            }
            else -> {
            }
        }
    }
}