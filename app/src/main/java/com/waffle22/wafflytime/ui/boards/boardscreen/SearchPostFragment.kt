package com.waffle22.wafflytime.ui.boards.boardscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffle22.wafflytime.databinding.FragmentSearchPostBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchPostFragment : Fragment() {
    private lateinit var binding: FragmentSearchPostBinding

    private val viewModel: BoardViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        binding.text.setText("")
        viewModel.searchPost("")
        showSearchResultLogic()

        val searchResultAdapter = PostPreviewAdapter{
            val action = SearchPostFragmentDirections.actionSearchPostFragmentToPostFragment(it.boardId, it.postId)
            this.findNavController().navigate(action)
        }
        viewModel.searchResults.observe(this.viewLifecycleOwner){ items ->
            items.let{
                searchResultAdapter.submitList(it)
            }
        }
        binding.searchResult.adapter = searchResultAdapter
        binding.searchResult.layoutManager = LinearLayoutManager(this.context)

        binding.searchButton.setOnClickListener {
            viewModel.searchPost(binding.text.text.toString())
            showSearchResultLogic()
        }
        */
        binding.backButton.setOnClickListener {findNavController().navigateUp()}
    }

    /*
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
     */
}