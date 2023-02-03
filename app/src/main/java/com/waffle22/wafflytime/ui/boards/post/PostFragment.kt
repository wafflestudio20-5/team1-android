package com.waffle22.wafflytime.ui.boards.post

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.waffle22.wafflytime.R
import com.waffle22.wafflytime.databinding.FragmentPostBinding
import com.waffle22.wafflytime.network.dto.LoadingStatus
import com.waffle22.wafflytime.network.dto.PostTaskType
import com.waffle22.wafflytime.network.dto.ReplyResponse
import com.waffle22.wafflytime.network.dto.TimeDTO
import com.waffle22.wafflytime.ui.boards.boardscreen.BoardViewModel
import com.waffle22.wafflytime.util.timeToString
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.LocalDate

class PostFragment() : Fragment() {
    private lateinit var binding: FragmentPostBinding

    private val viewModel: PostViewModel by sharedViewModel()
    private val boardViewModel: BoardViewModel by sharedViewModel()
    private val navigationArgs: PostFragmentArgs by navArgs()

    private var boardId = 0L
    private var postId = 0L
    private var replyParent: Long? = null
    private var editingReply: ReplyResponse? = null
    private var isSetUpMenu: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        boardId = navigationArgs.boardId
        postId = navigationArgs.postId
        viewModel.initalization()
        viewModel.refresh(boardId, postId)
    }

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

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh(boardId, postId)
        }

        //게시글 부분
        viewModel.getPost(boardId, postId)

        lifecycleScope.launch {
            viewModel.postState.collect {
                showPostLogic(it)
            }
        }

        // 게시물 이미지
        val postImageAdapter = PostImageAdapter()
        viewModel.images.observe(this.viewLifecycleOwner) { items ->
            items.let{
                postImageAdapter.submitList(it.toList())
            }
        }
        binding.images.adapter = postImageAdapter
        binding.images.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)

        // 댓글 부분
        val postReplyAdapter = PostReplyAdapter(
            {setReplyState(it.replyId)},
            {viewModel.canEditReply(it.isMyReply)},
            {flag, reply -> modifyReplyLogic(flag, reply)},
            {reply -> moveToNewChat(reply.replyId)}
        )
        binding.comments.adapter = postReplyAdapter

        binding.comments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤이 끝에 도달했는지 확인
                if (!binding.comments.canScrollVertically(1)) {
                    viewModel.getReplies(boardId, postId)
                }
            }
        })


        viewModel.replies.observe(this.viewLifecycleOwner){ items ->
            items.let{
                postReplyAdapter.submitList(it.toList())
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.repliesState.collect{
                    showRepliesLogic(it)
                }
            }
        }

        //툴바
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        //새 댓글 작성
        binding.newCommentButton.setOnClickListener {
            if (editingReply == null) {
                viewModel.createReply(
                    binding.newCommentText.text.toString(),
                    replyParent,
                    binding.anonymous.isChecked
                )
                viewModel.refresh(boardId, postId)
                binding.newCommentText.setText("")
                setReplyState(null)
            }
            else {
                editReply(binding.newCommentText.text.toString())
            }
            replyParent = null
        }
        setReplyState(null)
    }

    private fun setUpMenu(){
        isSetUpMenu = true
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
                    R.id.dm -> {
                        val action = PostFragmentDirections.actionPostFragmentToNewChatFragment(boardId, postId)
                        findNavController().navigate(action)
                    }
                    R.id.delete -> {
                        viewModel.deletePost()
                        boardViewModel.setRefresh()
                        findNavController().navigateUp()
                    }
                }
                return true
            }
        })
    }

    private fun showPostLogic(status: PostStatus){
        when (status){
            PostStatus.StandBy -> Toast.makeText(context, "게시물 로딩중", Toast.LENGTH_SHORT).show()
            PostStatus.Success -> {
                if(isSetUpMenu) null else setUpMenu()
                Toast.makeText(context, "게시물 로딩 완료!", Toast.LENGTH_SHORT).show()
                binding.swipeRefreshLayout.isRefreshing = false
                binding.apply {
                    binding.toolbar.title = viewModel.curBoard!!.title
                    nickname.text = viewModel.curPost.value!!.nickname ?: "익명"
                    time.text = viewModel.curPost.value!!.createdAt.timeToString()
                    if (viewModel.curPost.value!!.title != null) {
                        title.text = viewModel.curPost.value!!.title
                        title.visibility = View.VISIBLE
                    }
                    else    title.visibility = View.GONE
                    mainText.text = viewModel.curPost.value!!.contents
                    likesText.text = viewModel.curPost.value!!.nlikes.toString()
                    commentsText.text = viewModel.curPost.value!!.nreplies.toString()
                    scrapsText.text = viewModel.curPost.value!!.nreplies.toString()
                }
                //좋아요, 스크랩 바인딩
                binding.likePost.setOnClickListener { viewModel.likePost() }
                binding.scrapPost.setOnClickListener { viewModel.scrapPost() }
            }
            else -> {
                Toast.makeText(context, "오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRepliesLogic(status: PostStatus){
        when (status){
            PostStatus.StandBy -> Toast.makeText(context, "댓글 로딩중", Toast.LENGTH_SHORT).show()
            PostStatus.Success -> {
                Toast.makeText(context, "댓글 로딩 완료", Toast.LENGTH_SHORT).show()
                Log.v("PostFragment", "댓글 받아오기 성공!")
            }
            else -> {
                Toast.makeText(context, "오류", Toast.LENGTH_SHORT).show()
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

    private fun modifyReplyLogic(isEdit: Boolean, reply: ReplyResponse){
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
                        LoadingStatus.Standby -> Toast.makeText(context, "댓글 수정중", Toast.LENGTH_SHORT).show()
                        LoadingStatus.Success -> {
                            Toast.makeText(context, "댓글 수정 완료", Toast.LENGTH_SHORT).show()
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
                        LoadingStatus.Standby -> Toast.makeText(context, "댓글 삭제중", Toast.LENGTH_SHORT).show()
                        LoadingStatus.Success -> {
                            Toast.makeText(context, "댓글 삭제 완료", Toast.LENGTH_SHORT).show()
                            viewModel.refresh(boardId, postId)
                        }
                        else -> null
                    }
                }
            }
        }
    }

    private fun moveToNewChat(replyId: Long) {
        val action = PostFragmentDirections.actionPostFragmentToNewChatFragment(boardId, postId, replyId)
        findNavController().navigate(action)
    }
}