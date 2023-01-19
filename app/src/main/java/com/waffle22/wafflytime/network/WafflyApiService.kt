package com.waffle22.wafflytime.network

import com.waffle22.wafflytime.network.dto.*
import org.json.JSONArray
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
    
    // Board 관련
    @GET("/api/board/{boardId}")
    suspend fun getSingleBoard(
        //@Header("Authorization") token: String,
        boardId: Long
    ): Response<BoardDTO>

    @GET("/api/boards")
    suspend fun getAllBoards(@Header("Authorization") token: String): Response<List<BoardListResponse>>

    @POST("/api/board")
    suspend fun createBoard(
        //@Header("Authorization") token: String,
        @Body() boardDTO: BoardDTO
    ): Response<CreateBoardResponse>

    @DELETE("/api/board/{boardId}")
    suspend fun deleteBoard(
        //@Header("Authorization") token: String,
        boardId: Long
    ): Response<DeleteBoardResponse>

    // Post 관련
    @GET("/api/board/{boardId}/post/{postId}")
    suspend fun getSinglePost(
        @Header("Authorization") token: String,
        boardId: Long, postId: Long
    ): Response<PostResponse>

    @GET("/api/board/{boardId}/posts?")
    suspend fun getAllPosts(
        @Header("Authorization") token: String,
        boardId: Long, @Body() page: Int, @Body() size: Int
    ): Response<PostsPage>

    @GET("/api/user/mypost?")
    suspend fun getMyPosts(
        @Header("Authorization") token: String,
        @Body() page: Int, @Body() size: Int
    ): Response<PostsPage>

    @GET("/api/user/myscrap?")
    suspend fun getMyScraps(
        @Header("Authorization") token: String,
        @Body() page: Int, @Body() size: Int
    ): Response<PostsPage>

    @POST("/api/board/{boardId}/post")
    suspend fun createPost(
        //@Header("Authorization") token: String,
        boardId: Long,
        @Body() postRequest: PostRequest
    ): Response<PostRequest>

    @DELETE("/api/board/{boardId}/post/{postId}")
    suspend fun deletePost(boardId: Long, postId: Long): Response<DeletePostResponse>

    @PUT("/api/board/{boardId}/post/{postId}")
    suspend fun editPost(boardId: Long, postId: Long, @Body() editPostRequest: EditPostRequest): Response<EditPostResponse>
}