package com.waffle22.wafflytime.ui.boards.postscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffle22.wafflytime.databinding.FragmentPostBinding

class PostFragment(
    private val boardId: Long,
    private val postId: Long
) : Fragment() {
    private lateinit var binding: FragmentPostBinding

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPost(boardId, postId)

        lifecycleScope.launchWhenStarted {
            viewModel.postState.collect {
                showPostLogic(it)
            }
        }

        val postCommentAdapter = PostCommentAdapter()
        viewModel.comments.observe(this.viewLifecycleOwner){items ->
            items.let{
                postCommentAdapter.submitList(it)
            }
        }
        binding.comments.adapter = postCommentAdapter
        binding.comments.layoutManager = LinearLayoutManager(this.context)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showPostLogic(status: PostStatus){
        when (status){
            PostStatus.StandBy -> null
            PostStatus.Success -> {
                binding.apply {
                    binding.toolbar.title = viewModel.curBoard.title
                    nickname.text = viewModel.curPost.value!!.nickname ?: "익명"
                    //time.text = viewModel.curPost.value!!.time
                    mainText.text = viewModel.curPost.value!!.contents
                    //likesText.text = viewModel.curPost.value!!.likes.toString()
                    //commentsText.text = viewModel.curPost.value!!.comment_cnt.toString()
                    //scrapsText.text = viewModel.curPost.value!!.clipped.toString()
                }
            }
            else -> {
                binding.errorText.text = when(status){
                    PostStatus.NotFound -> "존재하지 않는 게시판입니다."
                    PostStatus.BadRequest -> "해당 게시판의 게시물이 아닙니다."
                    else -> "알 수 없는 오류"
                }
            }
        }
    }
}