package com.waffle22.wafflytime.ui.boards.threadscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffle22.wafflytime.databinding.FragmentThreadBinding

class ThreadFragment : Fragment() {
    private lateinit var binding: FragmentThreadBinding

    private val viewModel: ThreadViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThreadBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            nickname.text = viewModel.waffThread.value!!.nickname
            time.text = viewModel.waffThread.value!!.time
            mainText.text = viewModel.waffThread.value!!.text
            likesText.text = viewModel.waffThread.value!!.likes.toString()
            commentsText.text = viewModel.waffThread.value!!.comment_cnt.toString()
            scrapsText.text = viewModel.waffThread.value!!.clipped.toString()
        }

        val threadCommentAdapter = ThreadCommentAdapter()
        viewModel.comments.observe(this.viewLifecycleOwner){items ->
            items.let{
                threadCommentAdapter.submitList(it)
            }
        }
        binding.comments.adapter = threadCommentAdapter
        binding.comments.layoutManager = LinearLayoutManager(this.context)
    }
}