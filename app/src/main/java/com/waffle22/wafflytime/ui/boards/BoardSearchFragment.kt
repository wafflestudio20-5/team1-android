package com.waffle22.wafflytime.ui.boards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffle22.wafflytime.databinding.FragmentSearchBoardBinding

class BoardSearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBoardBinding

    private val viewModel: BoardSearchViewModel by activityViewModels()

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

        if (viewModel.searchResults.value!!.isEmpty())
            binding.searchResult.visibility = View.GONE
        else    binding.noSearchResult.visibility = View.GONE

        val boardSearchAdapter  = BoardSearchAdapter{
            val action = BoardSearchFragmentDirections.actionBoardSearchFragmentToBoardFragment()
            this.findNavController().navigate(action)
        }
        viewModel.searchResults.observe(this.viewLifecycleOwner) { items ->
            items.let{
                boardSearchAdapter.submitList(it)
            }
        }
        binding.searchResult.adapter = boardSearchAdapter
        binding.searchResult.layoutManager = LinearLayoutManager(this.context)
    }
}