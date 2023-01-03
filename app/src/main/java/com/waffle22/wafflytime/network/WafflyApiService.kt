package com.waffle22.wafflytime.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.waffle22.wafflytime.network.dto.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WafflyApiService {
    // TODO: 이곳에 백엔드 api 추가하면 될것 같아요
    /*
    @GET("login")
    suspend fun getLogin(): List<WafflyLogin>
    */
    @POST("/api/auth/local/login")
    suspend fun basicLogin(@Body() request: LoginRequest): AccessTokenContainer

    @POST("/api/auth/local/signup")
    suspend fun signUp(@Body() request: SignUpRequest): AccessTokenContainer
}