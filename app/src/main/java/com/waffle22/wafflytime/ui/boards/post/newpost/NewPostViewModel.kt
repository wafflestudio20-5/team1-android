package com.waffle22.wafflytime.ui.boards.post.newpost

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.network.dto.*
import com.waffle22.wafflytime.util.parseError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

data class ImageStorage(
    val imageRequest: ImageRequest,
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

class NewPostViewModel(
    private val wafflyApiService: WafflyApiService,
    private val moshi: Moshi
) : ViewModel() {
    private var _boardInfo = MutableLiveData<BoardDTO>()
    val boardInfo: LiveData<BoardDTO>
        get() = _boardInfo
    private var _images = MutableLiveData<MutableList<ImageStorage>>()
    val images : LiveData<MutableList<ImageStorage>>
        get() = _images

    private var _boardLoadingStatus = MutableStateFlow(LoadingStatus.Standby)
    val boardLoadingStatus: StateFlow<LoadingStatus>
        get() = _boardLoadingStatus
    private var _createPostStatus = MutableStateFlow(LoadingStatus.Standby)
    val createPostStatus: StateFlow<LoadingStatus>
        get() = _createPostStatus
    var errorMessage = ""

    fun getBoardInfo(boardId: Long){
        viewModelScope.launch {
            try{
                val response = wafflyApiService.getSingleBoard(boardId)
                when(response.code().toString()){
                    "200" -> {
                        Log.v("BoardViewModel", response.body()!!.title)
                        _boardInfo.value = response.body()
                        _boardLoadingStatus.value = LoadingStatus.Success
                    }
                    else -> {
                        Log.v("BoardViewModel", response.errorBody()!!.string())
                        _boardLoadingStatus.value = LoadingStatus.Error
                        errorMessage = HttpException(response).parseError(moshi)!!.message
                    }
                }
            } catch (e: java.lang.Exception){
                _boardLoadingStatus.value = LoadingStatus.Corruption
                Log.v("BoardViewModel", e.toString())
            }
        }
    }

    fun createNewPost(title: String?, contents: String, isQuestion: Boolean, isAnonymous: Boolean){
        viewModelScope.launch {
            try {
                var requestImages = mutableListOf<ImageRequest>()
                for(image in _images.value!!)
                    requestImages += image.imageRequest
                val request = PostRequest(title, contents, isQuestion, isAnonymous, requestImages)
                val response = wafflyApiService.createPost(_boardInfo.value!!.boardId, request)
                when (response.code().toString()){
                    "200" -> {
                        Log.v("NewPostViewModel", "New Post Created")
                        if (response.body()!!.images != null) {
                            for (i in 0 until _images.value!!.size) {
                                val url = response.body()!!.images?.get(i)?.preSignedUrl
                                val imageResponse = url?.let {
                                    wafflyApiService.uploadImage(
                                        it,
                                        _images.value!![i].byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                                    )
                                }
                                if (url != null) {
                                    Log.d("NewPostViewModel", url)
                                }
                            }
                        }
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
    }

    fun editPost(post: PostResponse, newTitle: String?, newContents: String){
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

    }

    fun addNewImage(filename: String, byteArray: ByteArray){
        if (_images.value == null)  _images.value = mutableListOf()
        _images.value!! += ImageStorage(ImageRequest(_images.value!!.size, filename, ""),byteArray)
    }

    fun startEditImageDescription(imageRequest: ImageRequest){
        //dialog 띄우기
    }

    fun deleteImage(imageRequest: ImageRequest){

    }

    fun resetStates(){
        _createPostStatus.value = LoadingStatus.Standby
        _boardLoadingStatus.value = LoadingStatus.Standby
        _images.value = mutableListOf()
    }
}