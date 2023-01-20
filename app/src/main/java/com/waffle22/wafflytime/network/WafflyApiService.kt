package com.waffle22.wafflytime.network

import com.waffle22.wafflytime.network.dto.*
import retrofit2.Response
import retrofit2.http.*

interface WafflyApiService {
    // TODO: 이곳에 백엔드 api 추가하면 될것 같아요
    /*
    @GET("login")
    suspend fun getLogin(): List<WafflyLogin>
    */
    @POST("/api/auth/local/login")
    suspend fun basicLogin(@Body() request: LoginRequest): Response<TokenContainer>

    @POST("/api/auth/local/signup")
    suspend fun signUp(@Body() request: SignUpRequest): Response<TokenContainer>

    @PUT("/api/auth/refresh")
    suspend fun refresh(@Header("Authorization") token: String): Response<TokenContainer>


    // Board 관련
    @GET("/api/board/{boardId}")
    suspend fun getSingleBoard(boardId: Long): BoardDTO

    @GET("/api/boards")
    suspend fun getAllBoards(): List<BoardDTO>

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