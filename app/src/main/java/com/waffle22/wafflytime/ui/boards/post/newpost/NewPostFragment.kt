package com.waffle22.wafflytime.ui.boards.post.newpost

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffle22.wafflytime.databinding.FragmentNewPostBinding
import com.waffle22.wafflytime.network.dto.PostTaskType
import com.waffle22.wafflytime.ui.boards.boardscreen.BoardViewModel
import com.waffle22.wafflytime.util.SlackState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.ByteArrayOutputStream

class NewPostFragment : Fragment() {
    private lateinit var binding: FragmentNewPostBinding

    private val viewModel: NewPostViewModel by sharedViewModel()
    private val boardViewModel: BoardViewModel by sharedViewModel()

    private val navigationArgs: NewPostFragmentArgs by navArgs()
    private lateinit var getImage: ActivityResultLauncher<Intent>

    private var boardId = 0L
    private var postId = 0L
    private lateinit var taskType: PostTaskType

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPostBinding.inflate(inflater,container,false)
        getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ res ->
            addImageReceiver(res)
        }
        boardId = navigationArgs.boardId
        postId = navigationArgs.postId
        taskType = navigationArgs.taskType
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initViewModel(boardId, postId, taskType)

        lifecycleScope.launch {
            viewModel.boardInfoState.collect {
                setNewPostScreenLogic(it)
            }
        }
        lifecycleScope.launch {
           viewModel.createPostState.collect{
               submitPostLogic(it)
           }
        }

        val newPostImageAdapter = NewPostImageAdapter(
            {image, description -> viewModel.editImageDescription(image, description)},
            {viewModel.deleteImage(it)}
        )
        viewModel.images.observe(this.viewLifecycleOwner){ items ->
            items.let{
                newPostImageAdapter.submitList(it)
            }
        }
        binding.images.adapter = newPostImageAdapter
        binding.images.layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.HORIZONTAL, false)

        binding.toolbar.setNavigationOnClickListener {
            resetStates()
            findNavController().navigateUp()
        }
        binding.newImage.setOnClickListener{
            uploadNewImage()
        }
    }

    private fun resetStates() {
        binding.title.setText("")
        binding.contents.setText("")
    }

    fun setNewPostScreenLogic(state: SlackState<NewPostInfoHolder>){
        when (state.status){
            "0" -> null
            else -> {
                when (state.status) {
                    "200" -> {
                        binding.title.visibility =
                            if (state.dataHolder!!.boardInfo!!.boardType == "DEFAULT") View.VISIBLE else View.GONE
                        if (taskType == PostTaskType.EDIT){
                            if (state.dataHolder!!.boardInfo!!.boardType == "DEFAULT")
                                binding.title.setText(state.dataHolder!!.originalPost!!.title!!)
                            binding.contents.setText(state.dataHolder!!.originalPost!!.contents)
                        }
                        else{
                            binding.title.setText("")
                            binding.contents.setText("")
                        }
                        binding.submitButton.setOnClickListener {
                            if(state.dataHolder!!.boardInfo!!.boardType == "DEFAULT" && binding.title.text.toString() == "")
                                Toast.makeText(context,"제목은 비워 둘 수 없습니다", Toast.LENGTH_SHORT).show()
                            else if (binding.contents.text.toString() == "")
                                Toast.makeText(context, "내용은 비워 둘 수 없습니다", Toast.LENGTH_SHORT).show()
                            else {
                                if (taskType == PostTaskType.CREATE)
                                    viewModel.submitPost(
                                        if (state.dataHolder!!.boardInfo!!.boardType == "DEFAULT") binding.title.text.toString() else null,
                                        binding.contents.text.toString(),
                                        binding.isQuestion.isChecked,
                                        binding.isAnonymous.isChecked
                                    )
                                else viewModel.editPost(
                                    if (state.dataHolder!!.boardInfo!!.boardType == "DEFAULT") binding.title.text.toString() else null,
                                    binding.contents.text.toString()
                                )
                            }
                        }
                    }
                    else -> {
                        Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun submitPostLogic(state: SlackState<PostResponseHolder>){
        when(state.status){
            "0" -> null
            else -> {
                when (state.status){
                    "200" -> {
                        if(taskType == PostTaskType.CREATE) boardViewModel.setRefresh() else null //TODO
                        findNavController().navigateUp()
                    }
                    else -> {
                        Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun uploadNewImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "image/jpg"))
        getImage.launch(intent)
    }

    fun addImageReceiver(result: ActivityResult){
        if(result.resultCode == RESULT_OK){
            try {
                result.data?.data?.let { returnUri ->
                    val filename = requireContext().contentResolver.query(
                        returnUri,
                        null,
                        null,
                        null,
                        null
                    )!!.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        cursor.moveToFirst()
                        cursor.getString(nameIndex)
                    }
                    Log.v("NewPostFragment", "Get Image filename")
                    val byteArray =
                        requireContext().contentResolver.openInputStream(returnUri)!!
                            .use { stream ->
                                val bytestream = ByteArrayOutputStream()
                                val buffer = ByteArray(1000)
                                var size: Int
                                while(true){
                                    size = stream.read(buffer)
                                    if(size == -1)  break
                                    bytestream.write(buffer, 0, size)
                                }
                                bytestream.toByteArray()
                            }
                    Log.v("NewPostFragment", "Get Image as ByteArray")
                    viewModel.addNewImage(filename, byteArray)
                }
            } catch (e: java.lang.Exception) {
                Log.d("NewPostFragment", e.toString())
            }
        }
    }
}