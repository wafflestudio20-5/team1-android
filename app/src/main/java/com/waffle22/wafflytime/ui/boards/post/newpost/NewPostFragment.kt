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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.waffle22.wafflytime.databinding.FragmentNewPostBinding
import com.waffle22.wafflytime.network.dto.LoadingStatus
import com.waffle22.wafflytime.network.dto.PostTaskType
import com.waffle22.wafflytime.ui.boards.post.PostViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.ByteArrayOutputStream

class NewPostFragment : Fragment() {
    private lateinit var binding: FragmentNewPostBinding
    private val viewModel: NewPostViewModel by sharedViewModel()
    private val postViewModel: PostViewModel by sharedViewModel()
    private val navigationArgs: NewPostFragmentArgs by navArgs()
    private lateinit var getImage: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPostBinding.inflate(inflater,container,false)
        getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ res ->
            addImageReceiver(res)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resetStates()

        if (navigationArgs.taskType == PostTaskType.EDIT){
            binding.title.setText(postViewModel.curPost.value!!.title?:"")
            binding.contents.setText(postViewModel.curPost.value!!.contents)
        }

        viewModel.getBoardInfo(navigationArgs.boardId)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.boardLoadingStatus.collect {
                    bindSubmitButton(it)
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createPostStatus.collect{
                    submitPostLogic(it)
                }
            }
        }

        val newPostImageAdapter = NewPostImageAdapter(
            {viewModel.startEditImageDescription(it)},
            {viewModel.deleteImage(it)}
        )
        viewModel.images.observe(this.viewLifecycleOwner){ items ->
            items.let{
                newPostImageAdapter.submitList(it)
            }
        }
        binding.images.adapter = newPostImageAdapter
        binding.images.layoutManager = LinearLayoutManager(this.context)

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
        viewModel.resetStates()
    }

    fun bindSubmitButton(status: LoadingStatus){
        when (status){
            LoadingStatus.Standby -> null
            LoadingStatus.Success -> {
                binding.title.visibility = if(viewModel.boardInfo.value!!.boardType == "DEFAULT") View.VISIBLE else View.GONE
                binding.submitButton.setOnClickListener{
                    if(navigationArgs.taskType == PostTaskType.CREATE)
                        viewModel.createNewPost(if(viewModel.boardInfo.value!!.boardType == "DEFAULT") binding.title.text.toString() else null,
                        binding.contents.text.toString(), binding.isQuestion.isChecked, binding.isAnonymous.isChecked)
                    else
                        viewModel.editPost(postViewModel.curPost.value!!,
                            if(viewModel.boardInfo.value!!.boardType == "DEFAULT") binding.title.text.toString() else null,
                            binding.contents.text.toString())
                }
            }
            else -> {
                Toast.makeText(context, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
                Log.v("NewPostFragment", "Board Loading Error")
            }
        }
    }

    fun submitPostLogic(status: LoadingStatus){
        when(status){
            LoadingStatus.Standby -> Toast.makeText(context, "로딩중", Toast.LENGTH_SHORT).show()
            LoadingStatus.Success -> {
                Toast.makeText(context, "업로드 성공", Toast.LENGTH_SHORT).show()
                resetStates()
                findNavController().navigateUp()
            }
            LoadingStatus.Error -> {
                Toast.makeText(context, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
                Log.v("BoardFragment", "Error occurred")
            }
            LoadingStatus.Corruption -> Toast.makeText(context, "알 수 없는 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
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
                    viewModel.addNewImage(filename, byteArray) //TODO
                }
            } catch (e: java.lang.Exception) {
                Log.d("NewPostFragment", e.toString())
            }
        }
    }
}