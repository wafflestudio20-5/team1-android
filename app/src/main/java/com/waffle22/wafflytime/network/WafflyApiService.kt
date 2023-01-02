package com.waffle22.wafflytime.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.waffle22.wafflytime.network.dto.LoginRequest
import com.waffle22.wafflytime.network.dto.LoginResponse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private const val BASE_URL = "http://api.wafflytime.com"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface WafflyApiService {
    // TODO: 이곳에 백엔드 api 추가하면 될것 같아요
    /*
    @GET("login")
    suspend fun getLogin(): List<WafflyLogin>
    */
    @POST("api/auth/local/login")
    suspend fun basicLogin(@Body() request: LoginRequest): LoginResponse
}

object WafflyApi {
    val retrofitService : WafflyApiService by lazy {
        retrofit.create(WafflyApiService::class.java)}
}