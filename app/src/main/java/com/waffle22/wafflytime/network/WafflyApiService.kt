package com.waffle22.wafflytime.network

import com.waffle22.wafflytime.network.dto.*
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
        @Path("boardId") boardId: Long
    ): Response<BoardDTO>

    @GET("/api/boards")
    suspend fun getAllBoards(): Response<List<BoardListResponse>>

    @POST("/api/board")
    suspend fun createBoard(
        @Body() createBoardRequest: CreateBoardRequest
    ): Response<CreateBoardResponse>

    @DELETE("/api/board/{boardId}")
    suspend fun deleteBoard(
        @Path("boardId") boardId: Long
    ): Response<DeleteBoardResponse>

    // Post 관련
    @GET("/api/board/{boardId}/post/{postId}")
    suspend fun getSinglePost(
        @Path("boardId") boardId: Long, @Path("postId") postId: Long
    ): Response<PostResponse>

    @GET("/api/board/{boardId}/posts")
    suspend fun getAllPosts(
        @Path("boardId") boardId: Long, @Query("page") page: Int, @Query("size") size: Int
    ): Response<PostsPage>

    @GET("/api/user/mypost")
    suspend fun getMyPosts(
        @Query("page") page: Int, @Query("size") size: Int
    ): Response<PostsPage>

    @GET("/api/user/myscrap")
    suspend fun getMyScraps(
        @Query("page") page: Int, @Query("size") size: Int
    ): Response<PostsPage>

    @GET("/api/hotpost")
    suspend fun getHotPosts(
        @Query("page") page: Int, @Query("size") size: Int
    ): Response<PostsPage>

    @GET("/api/bestpost")
    suspend fun getBestPosts(
        @Query("page") page: Int, @Query("size") size: Int
    ): Response<PostsPage>

    @POST("/api/board/{boardId}/post")
    suspend fun createPost(
        @Path("boardId") boardId: Long,
        @Body() postRequest: PostRequest
    ): Response<PostRequest>

    @DELETE("/api/board/{boardId}/post/{postId}")
    suspend fun deletePost(
        @Path("boardId") boardId: Long,
        @Path("postId") postId: Long): Response<DeletePostResponse>

    @PUT("/api/board/{boardId}/post/{postId}")
    suspend fun editPost(
        @Path("boardId") boardId: Long,
        @Path("postId") postId: Long,
        @Body() editPostRequest: EditPostRequest): Response<PostResponse>

    @POST("/api/board/{boardId}/post/{postId}/like")
    suspend fun likePost(
        @Path("boardId") boardId: Long,
        @Path("postId") postId: Long
    ): Response<PostResponse>

    @POST("/api/board/{boardId}/post/{postId}/scrap")
    suspend fun scrapPost(
        @Path("boardId") boardId: Long,
        @Path("postId") postId: Long
    ): Response<PostResponse>

    @DELETE("/api/user/myscrap")
    suspend fun scrapCancel(
        @Query("post") postId: Long
    ): Response<cancelScrapResponse>

    //Reply 관련
    @GET("/api/board/{boardId}/post/{postId}/replies")
    suspend fun getReplies(
        @Path("boardId") boardId: Long, @Path("postId") postId: Long
    ): Response<RepliesPage>

    @POST("/api/board/{boardId}/post/{postId}/reply")
    suspend fun createReply(
        @Path("boardId") boardId: Long,
        @Path("postId") postId: Long,
        @Body() replyRequest: ReplyRequest
    ): Response<ReplyResponse>

    @DELETE("/api/board/{boardId}/post/{postId}/reply/{replyId}")
    suspend fun deleteReply(
        @Path("boardId") boardId: Long,
        @Path("postId") postId: Long,
        @Path("replyId") replyId: Long
    ): Response<ReplyResponse>

    @PUT("/api/board/{boardId}/post/{postId}/reply/{replyId}")
    suspend fun editReply(
        @Path("boardId") boardId: Long,
        @Path("postId") postId: Long,
        @Path("replyId") replyId: Long,
        @Body() contents: String
    ): Response<ReplyResponse>
}