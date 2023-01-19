package com.waffle22.wafflytime.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.ui.boards.boardlist.BoardListViewModel
import com.waffle22.wafflytime.ui.boards.boardscreen.BoardViewModel
import com.waffle22.wafflytime.ui.boards.postscreen.PostViewModel
import com.waffle22.wafflytime.ui.login.LoginViewModel
import com.waffle22.wafflytime.ui.login.SignUpViewModel
import com.waffle22.wafflytime.ui.mainpage.MainHomeViewModel
import com.waffle22.wafflytime.util.AuthStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val appModule = module {
    single<Retrofit> {
        val context: Context = get()
        val sharedPreference =
            context.getSharedPreferences(AuthStorage.SharedPreferenceName, Context.MODE_PRIVATE)
        Retrofit.Builder()
            .baseUrl("http://api.wafflytime.com")
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    /*.addInterceptor {
                        val newRequest = it.request().newBuilder()
                            .addHeader(
                                "Authorization",
                                "Bearer " + sharedPreference.getString(
                                    AuthStorage.AccessTokenKey,
                                    ""
                                )
                            )
                            .build()
                        it.proceed(newRequest)
                    }*/
                    .build()
            )
            .build()
    }

    single<WafflyApiService> {
        get<Retrofit>().create(WafflyApiService::class.java)
    }

    single { AuthStorage(get()) }


    single<Moshi> {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    // 이곳에 inject 되는 viewModel 추가
    /*
    viewModel { UserViewModel(get(), get(), get()) }
    viewModel { PostListViewModel(get(), get()) }
    viewModel { (postId: Int) -> PostDetailViewModel(postId, get(), get(), get()) }
*/
    viewModel { LoginViewModel(get(), get()) }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { MainHomeViewModel(get(), get()) }
    viewModel { BoardListViewModel(get(), get(), get()) }
    viewModel { BoardViewModel(get(), get(), get()) }
    viewModel { PostViewModel(get()) }
}

