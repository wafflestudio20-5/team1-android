package com.waffle22.wafflytime.ui.boards.boardlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffle22.wafflytime.databinding.FragmentBoardListBinding
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
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(it.boardId)
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
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(it.boardId)
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

        //TODO: 내 게시물 관련 기능들과 연결
        binding.myPosts.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(0)
            this.findNavController().navigate(action)
        }
        binding.myComments.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(0)
            this.findNavController().navigate(action)
        }
        binding.myScraps.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(0)
            this.findNavController().navigate(action)
        }
        binding.hotBoard.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(0)
            this.findNavController().navigate(action)
        }
        binding.bestBoard.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment(0)
            this.findNavController().navigate(action)
        }

        binding.search.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardSearchFragment()
            this.findNavController().navigate(action)
        }
    }

    private fun showBoardsLogic(status: BoardLoadingStatus){
        when (status){
            BoardLoadingStatus.Success -> {
                Log.v("BoardListFragment", "Board Loading Success")
            }
            BoardLoadingStatus.TokenExpired -> {
                viewModel.refresh()
                viewModel.getAllBoards()
            }
            else -> {
            }
        }
    }
}