package com.waffle22.wafflytime.ui.boards.boardlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffle22.wafflytime.databinding.FragmentSearchBoardBinding
import com.waffle22.wafflytime.network.dto.BoardType
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BoardSearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBoardBinding

    private val viewModel: BoardListViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBoardBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.text.setText("")
        viewModel.searchBoard("")
        showSearchResultLogic()

        val boardSearchAdapter  = BoardListAdapter{
            val action = BoardSearchFragmentDirections.actionBoardSearchFragmentToBoardFragment(it.boardId, BoardType.Common)
            this.findNavController().navigate(action)
        }
        viewModel.searchResults.observe(this.viewLifecycleOwner) { items ->
            items.let{
                boardSearchAdapter.submitList(it)
            }
        }
        binding.searchResult.adapter = boardSearchAdapter
        binding.searchResult.layoutManager = LinearLayoutManager(this.context)

        binding.searchButton.setOnClickListener {
            viewModel.searchBoard(binding.text.text.toString())
            showSearchResultLogic()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.newBoard.setOnClickListener {
            val action = BoardSearchFragmentDirections.actionBoardSearchFragmentToNewBoardFragment()
            this.findNavController().navigate(action)
        }
    }

    private fun showSearchResultLogic(){
        if (viewModel.searchResults.value!!.isEmpty()) {
            binding.searchResult.visibility = View.GONE
            binding.noSearchResult.visibility = View.VISIBLE
        }
        else{
            binding.searchResult.visibility = View.VISIBLE
            binding.noSearchResult.visibility = View.GONE
        }
    }
}