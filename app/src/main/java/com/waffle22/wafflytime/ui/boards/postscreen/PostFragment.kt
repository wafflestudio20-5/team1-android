package com.waffle22.wafflytime.ui.boards.postscreen

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentPostBinding
import com.waffle22.wafflytime.network.dto.LoadingStatus
import com.waffle22.wafflytime.network.dto.PostTaskType
import com.waffle22.wafflytime.network.dto.ReplyResponse
import com.waffle22.wafflytime.network.dto.TimeDTO
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.LocalDate

class PostFragment() : Fragment() {
    private lateinit var binding: FragmentPostBinding

    private val viewModel: PostViewModel by sharedViewModel()
    private val navigationArgs: PostFragmentArgs by navArgs()

    private var boardId = 0L
    private var postId = 0L
    private var replyParent: Long? = null
    private var editingReply: ReplyResponse? = null

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
        setUpMenu()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh(boardId, postId)
        }

        //게시글 부분
        boardId = navigationArgs.boardId
        postId = navigationArgs.postId
        viewModel.getPost(boardId, postId)

        lifecycleScope.launchWhenStarted {
            viewModel.postState.collect {
                showPostLogic(it)
            }
        }

        // 댓글 부분
        val postReplyAdapter = PostReplyAdapter(
            {setReplyState(it.replyId)},
            {viewModel.canEditReply(it)},
            {flag, reply -> modifyReplyLogic(flag, reply)}
        )
        viewModel.replies.observe(this.viewLifecycleOwner){ items ->
            items.let{
                postReplyAdapter.submitList(it)
            }
        }
        binding.comments.adapter = postReplyAdapter
        binding.comments.layoutManager = LinearLayoutManager(this.context)

        viewModel.getReplies(boardId, postId)
        lifecycleScope.launchWhenStarted {
            viewModel.repliesState.collect{
                showRepliesLogic(it)
            }
        }

        //툴바
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        //새 댓글 작성
        //{"timestamp":"2023-01-20T09:55:13.843714127","status":500,"error-code":0,"default-message":"could not execute statement; SQL [n/a]; constraint [reply.post_id]"}
        binding.newCommentButton.setOnClickListener {
            if (editingReply == null) {
                viewModel.createReply(
                    binding.newCommentText.text.toString(),
                    replyParent,
                    binding.anonymous.isChecked
                )
                viewModel.getReplies(boardId, postId)
                binding.newCommentText.setText("")
                setReplyState(null)
            }
            else {
                editReply(binding.newCommentText.text.toString())
            }
        }
        setReplyState(null)
    }

    private fun setUpMenu(){
        binding.toolbar.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.post_actions, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                if(viewModel.canEditPost()){
                    menu.findItem(R.id.dm).isVisible = false
                }
                else {
                    menu.findItem(R.id.edit).isVisible = false
                    menu.findItem(R.id.delete).isVisible = false
                }
                super.onPrepareMenu(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.refresh -> viewModel.refresh(boardId, postId)
                    R.id.edit -> {
                        val action = PostFragmentDirections.actionPostFragmentToNewPostFragment(boardId, PostTaskType.EDIT)
                        findNavController().navigate(action)
                    }
                    R.id.delete -> {
                        viewModel.deletePost()
                        findNavController().navigateUp()
                    }
                }
                return true
            }
        })
    }

    private fun showPostLogic(status: PostStatus){
        when (status){
            PostStatus.StandBy -> null
            PostStatus.Success -> {
                binding.apply {
                    binding.toolbar.title = viewModel.curBoard.title
                    nickname.text = viewModel.curPost.value!!.nickname ?: "익명"
                    time.text = timeToText(viewModel.curPost.value!!.createdAt)
                    mainText.text = viewModel.curPost.value!!.contents
                    likesText.text = viewModel.curPost.value!!.nlikes.toString()
                    commentsText.text = viewModel.curPost.value!!.nreplies.toString()
                    scrapsText.text = viewModel.curPost.value!!.nreplies.toString()
                }
            }
            else -> {
                binding.errorText.text = when(status){
                    PostStatus.NotFound -> "존재하지 않는 게시물입니다."
                    PostStatus.BadRequest -> "해당 게시판의 게시물이 아닙니다."
                    else -> "알 수 없는 오류"
                }
            }
        }
    }

    private fun showRepliesLogic(status: PostStatus){
        when (status){
            PostStatus.StandBy -> null
            PostStatus.Success -> {
                Log.v("PostFragment", "댓글 받아오기 성공!")
            }
            else -> {
                binding.errorText.text = when(status){
                    PostStatus.NotFound -> "존재하지 않는 게시물입니다."
                    PostStatus.BadRequest -> "해당 게시판의 게시물이 아닙니다."
                    else -> "알 수 없는 오류"
                }
            }
        }
    }

    private fun setReplyState(parentId: Long?){
        Log.d("PostFragment", "setReplyState: "+parentId.toString())
        binding.newCommentText.setText("")
        replyParent = if(replyParent == parentId) null else parentId
        if (replyParent == null){
            binding.newCommentText.hint = "댓글을 입력하세요"
        }
        else {
            binding.newCommentText.hint = "답글을 입력하세요"
        }
    }

    fun modifyReplyLogic(isEdit: Boolean, reply: ReplyResponse){
        if (isEdit) setEditReply(reply)
        else    deleteReply(reply)
    }

    private fun setEditReply(reply: ReplyResponse){
        binding.newCommentText.setText(reply.contents)
        editingReply = reply
    }

    private fun editReply(contents: String){
        viewModel.editReply(editingReply!!, contents)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.modifyReplyState.collect {
                    when(it){
                        LoadingStatus.Standby -> null
                        LoadingStatus.Success -> {
                            viewModel.refresh(boardId, postId)
                            editingReply = null
                        }
                        else -> null
                    }
                }
            }
        }
    }

    private fun deleteReply(reply: ReplyResponse){
        viewModel.deleteReply(reply)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.modifyReplyState.collect{
                    when(it){
                        LoadingStatus.Standby -> null
                        LoadingStatus.Success -> {
                            viewModel.refresh(boardId, postId)
                        }
                        else -> null
                    }
                }
            }
        }
    }

    fun timeToText(time: TimeDTO): String{
        var timeText = time.month.toString() + '/' + time.day.toString() + ' ' + time.hour.toString() + ':' + time.minute.toString()
        if (LocalDate.now().year != time.year)
            timeText = time.year.toString() + '/' + timeText
        return timeText
    }
}