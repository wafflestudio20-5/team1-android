package com.waffle22.wafflytime.ui.boards.boardscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffle22.wafflytime.databinding.FragmentBoardBinding
import com.waffle22.wafflytime.network.dto.TimeDTO

class BoardFragment() : Fragment() {
    private lateinit var binding: FragmentBoardBinding

    private val viewModel: BoardViewModel by activityViewModels()

    private val navigationArgs: BoardFragmentArgs by navArgs()

    var boardId: Long = 0L

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

        val threadPreviewAdapter = ThreadPreviewAdapter{
            val action = BoardFragmentDirections.actionBoardFragmentToThreadFragment()
            this.findNavController().navigate(action)
        }
        viewModel.threads.observe(this.viewLifecycleOwner){ items ->
            items.let{
                threadPreviewAdapter.submitList(it)
            }
        }
        binding.threads.adapter = threadPreviewAdapter
        binding.threads.layoutManager = LinearLayoutManager(this.context)

        val boardAnnouncementAdapter = BoardAnnouncementAdapter{
            val action = BoardFragmentDirections.actionBoardFragmentToThreadFragment()
            this.findNavController().navigate(action)
        }
        viewModel.announcements.observe(this.viewLifecycleOwner){ items->
            items.let{
                boardAnnouncementAdapter.submitList(it)
            }
        }
        binding.announcements.adapter = boardAnnouncementAdapter
        binding.announcements.layoutManager = LinearLayoutManager(this.context)

        binding.newThread.setOnClickListener{
            val action = BoardFragmentDirections.actionBoardFragmentToNewThreadFragment()
            this.findNavController().navigate(action)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}