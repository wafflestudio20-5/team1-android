package com.waffle22.wafflytime.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.waffle22.wafflytime.network.dto.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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

}