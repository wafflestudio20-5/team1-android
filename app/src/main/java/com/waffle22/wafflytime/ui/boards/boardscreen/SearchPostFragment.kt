package com.waffle22.wafflytime.ui.boards.boardscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.databinding.FragmentSearchPostBinding
import com.waffle22.wafflytime.util.SlackState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchPostFragment : Fragment() {
    private lateinit var binding: FragmentSearchPostBinding
    private lateinit var searchResultAdapter: PostPreviewAdapter

    private val viewModel: SearchPostViewModel by sharedViewModel()
    private val navigationArgs: SearchPostFragmentArgs by navArgs()

    private var boardId: Long? = null

    private var holdFocus: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        boardId = navigationArgs.boardId
        viewModel.reInitViewModel()
    }

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


        searchResultAdapter = PostPreviewAdapter{
            val action = SearchPostFragmentDirections.actionSearchPostFragmentToPostFragment(it.boardId, it.postId)
            this.findNavController().navigate(action)
        }
        searchResultAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0 && !holdFocus){
                    binding.searchResult.scrollToPosition(0)
                } else{
                    holdFocus = false
                }
            }
        })

        lifecycleScope.launch {
            viewModel.searchPostState.collect{
                searchResultLogic(it)
            }
        }

        binding.apply{
            searchResult.adapter = searchResultAdapter
            searchResult.layoutManager = LinearLayoutManager(context)

            searchButton.setOnClickListener {
                viewModel.setRefresh()
                viewModel.requestData(boardId, binding.text.text.toString())
            }

            searchResult.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    // 스크롤이 끝에 도달했는지 확인
                    if (!searchResult.canScrollVertically(1)) {
                        viewModel.getBelowBoard(boardId, binding.text.text.toString())
                    }
                }
            })

            backButton.setOnClickListener {findNavController().navigateUp()}
        }
    }


    private fun searchResultLogic(state: SlackState<PostsPageHolder>){
        when(state.status){
            "0" -> null
            else -> {
                when (state.status) {
                    "200" -> {
                        val postsList = state.dataHolder!!.postsList!!.toList()
                        if(postsList.isEmpty()){
                            binding.searchResult.visibility = View.GONE
                            binding.noSearchResult.visibility = View.VISIBLE
                        }
                        else {
                            binding.searchResult.visibility = View.VISIBLE
                            binding.noSearchResult.visibility = View.GONE
                            searchResultAdapter.submitList(postsList)
                        }
                    }
                    else -> {
                        Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}