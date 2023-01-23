package com.waffle22.wafflytime.network

import com.waffle22.wafflytime.network.dto.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface WafflyApiService {
    // Auth 관련
    @POST("/api/auth/local/login")
    suspend fun basicLogin(@Body() request: LoginRequest): Response<TokenContainer>

    @POST("/api/auth/local/signup")
    suspend fun signUp(@Body() request: SignUpRequest): Response<TokenContainer>

    @PUT("/api/auth/refresh")
    suspend fun refresh(): Response<TokenContainer>

    @POST("/api/user/verify-mail")
    suspend fun emailAuth(@Body() email: EmailRequest): Response<EmailCode>

    @PATCH("/api/user/verified-mail")
    suspend fun emailPatch(@Body() email: EmailRequest): Response<TokenContainer>

    // 실험
    @GET("/api/user/me")
    suspend fun getUserInfo(): Response<UserDTO>

    @GET("/api/user/check/nickname/{nickname}")
    suspend fun checkNickname(@Path("nickname") nickname: String): Response<ResponseBody>

    @PUT("/api/user/me")
    suspend fun changeNickname(@Body() request: ChangeNicknameRequest): Response<UserDTO>

    @PUT("/api/user/me/profile")
    suspend fun setProfilePic(@Body() request: SetProfilePicRequest): Response<UserDTO>

    @PUT
    suspend fun uploadProfilePic(@Url preSignedUrl: String, @Body() request: RequestBody): Response<Unit>

    @DELETE("/api/user/me/profile")
    suspend fun deleteProfilePic(): Response<UserDTO>

    // Board 관련
    @GET("/api/board/0")
    suspend fun getSingleBoard(): Response<BoardDTO>

    @GET("/api/boards")
    suspend fun getAllBoards(@Header("Authorization") token: String): List<BoardDTO>

    @POST("/api/board")
    suspend fun createBoard(@Body() boardDTO: BoardDTO): CreateBoardResponse

    @DELETE("/api/board/{boardId}")
    suspend fun deleteBoard(boardId: Long): DeleteBoardResponse
    // Post 관련
    @GET("/api/board/{boardId}/post/{postId}")
    suspend fun getSinglePost(boardId: Long, postId: Long): PostResponse

    // Todo: pagenation 알아보고 구현
    /*
    @GET("/api/board/{boardId}/posts?page={page}&size={size}")
    suspend fun getAllPosts(boardId: Long, page: Int, size: Int): Page<PostDTO>
    */

    @POST("/api/board/{boardId}/post")
    suspend fun createPost(boardId: Long, @Body() postRequest: PostRequest): PostResponse

    @DELETE("/api/board/{boardId}/post/{postId}")
    suspend fun deletePost(boardId: Long, postId: Long): DeletePostResponse

    @PUT("/api/board/{boardId}/post/{postId}")
    suspend fun editPost(boardId: Long, postId: Long, @Body() editPostRequest: EditPostRequest): EditPostResponse
}