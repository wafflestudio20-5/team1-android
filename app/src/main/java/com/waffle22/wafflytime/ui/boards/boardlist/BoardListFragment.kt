package com.waffle22.wafflytime.ui.boards.boardlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffle22.wafflytime.databinding.FragmentBoardListBinding

class BoardListFragment : Fragment() {
    private lateinit var binding: FragmentBoardListBinding

    private val viewModel: BoardListViewModel by activityViewModels()

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

        binding.myPosts.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment()
            this.findNavController().navigate(action)
        }
        binding.myComments.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment()
            this.findNavController().navigate(action)
        }
        binding.myScraps.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment()
            this.findNavController().navigate(action)
        }
        binding.hotBoard.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment()
            this.findNavController().navigate(action)
        }
        binding.bestBoard.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment()
            this.findNavController().navigate(action)
        }

        val defaultBoardListAdapter = BoardListAdapter{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment()
            this.findNavController().navigate(action)
        }

        viewModel.defaultBoards.observe(this.viewLifecycleOwner){items->
            items.let{
                defaultBoardListAdapter.submitList(it)
            }
        }
        binding.defaultBoards.adapter = defaultBoardListAdapter
        binding.defaultBoards.layoutManager = LinearLayoutManager(this.context)

        val allBoardListAdapter = BoardListAdapter{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment()
            this.findNavController().navigate(action)
        }
        viewModel.allBoardsFiltered.observe(this.viewLifecycleOwner){items ->
            items.let{
                allBoardListAdapter.submitList(it)
            }
        }
        binding.allBoards.adapter = allBoardListAdapter
        binding.allBoards.layoutManager = LinearLayoutManager(this.context)

        val taggedBoardsAdapter = TaggedBoardsAdapter{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardFragment()
            this.findNavController().navigate(action)
        }
        viewModel.taggedBoards.observe(this.viewLifecycleOwner){ items ->
            items.let{
                taggedBoardsAdapter.submitList(it)
            }
        }
        binding.taggedBoards.adapter = taggedBoardsAdapter
        binding.taggedBoards.layoutManager = LinearLayoutManager(this.context)

        binding.search.setOnClickListener{
            val action = BoardListFragmentDirections.actionBoardListFragmentToBoardSearchFragment()
            this.findNavController().navigate(action)
        }
    }
}