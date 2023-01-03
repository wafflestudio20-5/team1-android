package com.waffle22.wafflytime.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffle22.wafflytime.databinding.FragmentBoardBinding

class BoardFragment : Fragment() {
    private lateinit var binding: FragmentBoardBinding

    private val viewModel: BoardViewModel by activityViewModels()

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

        val boardPreviewAdapter = BoardPreviewAdapter()
        viewModel.threads.observe(this.viewLifecycleOwner){ items ->
            items.let{
                boardPreviewAdapter.submitList(it)
            }
        }
        binding.threads.adapter = boardPreviewAdapter
        binding.threads.layoutManager = LinearLayoutManager(this.context)

        val boardAnnouncementAdapter = BoardAnnouncementAdapter()
        viewModel.announcements.observe(this.viewLifecycleOwner){ items->
            items.let{
                boardAnnouncementAdapter.submitList(it)
            }
        }
        binding.announcements.adapter = boardAnnouncementAdapter
        binding.announcements.layoutManager = LinearLayoutManager(this.context)
    }
}