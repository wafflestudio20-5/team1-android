package com.waffle22.wafflytime.ui.boards.post.newpost

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.*
import com.waffle22.wafflytime.ui.boards.boardscreen.NetWorkResultReturn
import com.waffle22.wafflytime.util.SlackState
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

data class ImageStorage(
    var imageRequest: ImageRequest,
    val byteArray: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageStorage

        if (imageRequest != other.imageRequest) return false
        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = imageRequest.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        return result
    }
}

data class NewPostInfoHolder(
    var boardInfo: BoardDTO?,
    var originalPost: PostResponse?
)
data class PostResponseHolder(
    var postResponse: PostResponse?
)

class NewPostViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
) : ViewModel() {
    private var _boardInfoState = MutableSharedFlow<SlackState<NewPostInfoHolder>>()
    val boardInfoState: SharedFlow<SlackState<NewPostInfoHolder>> = _boardInfoState
    private var _currentState: SlackState<NewPostInfoHolder> =
        SlackState("0", null, null, NewPostInfoHolder(null, null))

    private var _createPostState = MutableSharedFlow<SlackState<PostResponseHolder>>()
    val createPostState: SharedFlow<SlackState<PostResponseHolder>> = _createPostState
    private var _currentCreatePostState : SlackState<PostResponseHolder> =
        SlackState("0", null, null, PostResponseHolder(null))
    val currentCreatePostState: SlackState<PostResponseHolder> = _currentCreatePostState

    private var _images = MutableLiveData<MutableList<ImageStorage>>()
    val images : LiveData<MutableList<ImageStorage>> = _images

    // OnCreate
    fun initViewModel(boardId: Long, postId: Long, taskType: PostTaskType){
        viewModelScope.launch {
            val resultBoardInfo = getBoardInfo(boardId)
            val resultOriginalPost =
                if (taskType == PostTaskType.EDIT) getPostInfo(boardId, postId)
                else NetWorkResultReturn(true,"200",null,null)
            if (resultBoardInfo.done && resultOriginalPost.done){
                _currentState.apply{
                    status = "200"
                    errorCode = null
                    errorMessage = null
                }
            } else {
                val errorResult = if(!resultBoardInfo.done) resultBoardInfo else resultOriginalPost
                _currentState.apply{
                    status = errorResult.statusCode
                    errorCode = errorResult.errorCode
                    errorMessage = errorResult.errorMessage
                }
            }
            _images.value = mutableListOf()
            if (_currentState.dataHolder!!.originalPost != null) {
                // TODO: 이미지 ByteArray로 받아오기
            }
            _boardInfoState.emit(_currentState)
        }
    }

    suspend fun getBoardInfo(boardId: Long): NetWorkResultReturn {
        try{
            val response = wafflyApiService.getSingleBoard(boardId)
            if(response.isSuccessful) {
                _currentState.dataHolder!!.boardInfo = response.body()!!
            } else {
                val errorResponse = HttpException(response).parseError(moshi)!!
                _currentState.dataHolder!!.boardInfo = null
                return NetWorkResultReturn(false, errorResponse.statusCode, errorResponse.errorCode, errorResponse.message)
            }
        } catch (e: java.lang.Exception){
            _currentState.dataHolder!!.boardInfo = null
            return NetWorkResultReturn(false, "-1", null,"SystemCorruption")
        }
        return NetWorkResultReturn(true,"200",null,null)
    }

    // 게시물을 편집하는 경우 호출
    suspend fun getPostInfo(boardId: Long, postId: Long): NetWorkResultReturn{
        try{
            val response = wafflyApiService.getSinglePost(boardId, postId)
            if(response.isSuccessful) {
                _currentState.dataHolder!!.originalPost = response.body()!!
            } else {
                val errorResponse = HttpException(response).parseError(moshi)!!
                _currentState.dataHolder!!.originalPost = null
                return NetWorkResultReturn(false, errorResponse.statusCode, errorResponse.errorCode, errorResponse.message)
            }
        } catch (e: java.lang.Exception) {
            _currentState.dataHolder!!.originalPost = null
            return NetWorkResultReturn(false, "-1", null,"SystemCorruption")
        }
        return NetWorkResultReturn(true,"200",null,null)
    }

    fun submitPost(title: String?, contents: String, isQuestion: Boolean, isAnonymous: Boolean){
        viewModelScope.launch {
            try {
                var requestImages = mutableListOf<ImageRequest>()
                for(image in _images.value!!)
                    requestImages += image.imageRequest
                val request = PostRequest(title, contents, isQuestion, isAnonymous, requestImages)
                val response = wafflyApiService.createPost(_currentState.dataHolder!!.boardInfo!!.boardId, request)
                if (response.isSuccessful){
                    _currentCreatePostState.dataHolder!!.postResponse = response.body()
                    if (_currentCreatePostState.dataHolder!!.postResponse!!.images != null){
                        /*val originalImages = _currentState.dataHolder!!.originalPost!!.images ?: listOf()
                        val currentImages = _currentCreatePostState.dataHolder!!.postResponse!!.images!!
                            .filter{imageResponse ->  imageResponse in originalImages}*/
                        val currentImages = _currentCreatePostState.dataHolder!!.postResponse!!.images
                        for (image in currentImages!!){
                            for (imageStorage in _images.value!!){
                                if (image.imageId == imageStorage.imageRequest.imageId){
                                    val imageResponse = image.preSignedUrl.let{
                                        wafflyApiService.uploadImage(it,
                                            imageStorage.byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                                        )
                                    }
                                    if(!imageResponse.isSuccessful){
                                        Log.v("NewPostViewModel", "Cannot Uplaod Image")
                                    }
                                }
                            }
                        }
                    }
                    _currentCreatePostState.status = "200"
                    _currentCreatePostState.errorCode = null
                    _currentCreatePostState.errorMessage = null
                } else {
                    val errorResponse = HttpException(response).parseError(moshi)!!
                    _currentCreatePostState.status = errorResponse.statusCode
                    _currentCreatePostState.errorCode = errorResponse.errorCode
                    _currentCreatePostState.errorMessage = errorResponse.message
                }
            } catch(e: java.lang.Exception) {
                _currentCreatePostState.status = "-1"
                _currentCreatePostState.errorCode = null
                _currentCreatePostState.errorMessage = e.toString()
            }
            _createPostState.emit(_currentCreatePostState)
        }
    }

    /*fun editPost(post: PostResponse, newTitle: String?, newContents: String){
        viewModelScope.launch {
            try {
                val request = EditPostRequest(
                    newTitle,
                    newContents,
                    post.isQuestion,
                    post.isWriterAnonymous,
                    null,
                    null
                )
                Log.d("EditPost", "Made a request")
                val response = wafflyApiService.editPost(_boardInfo.value!!.boardId, post.postId, request)
                when (response.code().toString()){
                    "200" -> {
                        Log.v("NewPostViewModel", "New Post Created")
                        _createPostStatus.value = LoadingStatus.Success
                    }
                    else -> {
                        Log.v("NewPostViewModel", response.errorBody()!!.string())
                        errorMessage = HttpException(response).parseError(moshi)!!.message
                        _createPostStatus.value = LoadingStatus.Error
                    }
                }
            } catch(e: java.lang.Exception) {
                Log.v("NewPostViewModel", e.toString())
                _createPostStatus.value = LoadingStatus.Corruption
            }
        }

    }*/

    private fun newImageId(): Int{
        return if (_images.value!!.isNotEmpty()) _images.value!![_images.value!!.size-1].imageRequest.imageId + 1
        else 0
    }

    fun addNewImage(filename: String, byteArray: ByteArray){
        if (_images.value == null)  _images.value = mutableListOf()
        _images.value!! += ImageStorage(ImageRequest(newImageId(),filename, ""),byteArray)
    }

    fun editImageDescription(imageRequest: ImageRequest, newDescription: String){
        for (image in _images.value!!){
            if (image.imageRequest == imageRequest){
                image.imageRequest = ImageRequest(
                    imageRequest.imageId,
                    imageRequest.fileName,
                    newDescription
                )
                break
            }
        }
    }

    fun deleteImage(imageRequest: ImageRequest){
        _images.value!!.removeIf{imageStorage -> imageStorage.imageRequest == imageRequest}
    }
}