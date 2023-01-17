package com.waffle22.wafflytime.util

import android.content.SharedPreferences
import android.util.Log
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.dto.ErrorDTO
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit

class TokenInterceptor(
    private val sharedPreferences: SharedPreferences,
    private val authStorage: AuthStorage,
    private val moshi: Moshi
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var requestOriginal: Request = chain.request()

        // 1. Access Token 있으면 토큰을 헤더에 추가한다.
        val accessToken : String = getAccessToken()
        // 일부러 에러를 위해 access token 조작
        if (accessToken.isNotEmpty()) {
            requestOriginal = requestOriginal.putTokenHeader(accessToken)
        }
        // 2. 요청을 보내본다. 돌아온 요청을 파싱하여 토큰에 문제가 없으면 넘긴다. 문제있으면 로직 계속
        val responseOriginal = chain.proceed(requestOriginal)

        if (responseOriginal.isTokenInvalid()) {
            // 3. 현재 Access Token 에 문제가 있음으로 refresh 해야한다.
            val requestRefresh: Request = makeRefreshRequest()
            val responseRefresh = chain.proceed(requestRefresh)

            if (responseRefresh.isSuccessful) {
                // 4. 만약 refresh 요청이 정상적이라면 authStorage 를 통해 refresh 등록
                val tokenContainer = responseRefresh.parseRefresh(moshi)!!
                authStorage.setAuthInfo(tokenContainer.accessToken, tokenContainer.refreshToken)
                // 5. Access Token 새로 넣어서 요청 다시 보내기
                val newAccessToken: String = getAccessToken()
                val requestModified = requestOriginal.replaceTokenHeader(newAccessToken)
                // 6. modified 된 요청 반환
                return chain.proceed(requestModified)
            } 
            // 7. 만약 refresh 요청마저 실패했다면? -> 각각 viewModel 에서 로그아웃 처리할것ㅏㅡㅓ
            return responseRefresh
        }
        return responseOriginal
    }

    private fun getAccessToken(): String {
        return sharedPreferences.getString(AuthStorage.AccessTokenKey,"")?:""
    }

    private fun Request.putTokenHeader(accessToken: String): Request {
        return this.newBuilder()
            .addHeader("Authorization","Bearer $accessToken")
            .build()
    }

    private fun Request.replaceTokenHeader(accessToken: String): Request {
        return this.newBuilder()
            .header("Authorization","Bearer $accessToken")
            .build()
    }

    private fun makeRefreshRequest(): Request {
        val refreshToken: String = sharedPreferences.getString(AuthStorage.RefreshTokenKey,"")?:""
        return Request.Builder()
            .put("".toRequestBody())
            .url("http://api.wafflytime.com/api/auth/refresh")
            .addHeader("Content-Type","application/json")
            .addHeader("Authorization","Bearer $refreshToken")
            .build()
    }

    private fun Response.isTokenInvalid(): Boolean {
        // 1. 먼저 응답에 에러가 있는지 확인
        if (!this.isSuccessful){

            // 2. 에러가 있다면 파싱해보자.
            val errorResponse = this.parseError(moshi)
            val statusCode = errorResponse?.statusCode
            val errorCode = errorResponse?.errorCode

            // 3. 에러가 진짜 Access Token 문제때문에 생긴거였으면 true 반환(토큰문제 맞음)
            if ( statusCode == "401" && (errorCode=="103" || errorCode=="104") ) {
                return true
            }
        }
        
        // 4. 에러가 Access Token 문제가 아니면 false 반환(토큰문제 아님)
        return false
    }
}