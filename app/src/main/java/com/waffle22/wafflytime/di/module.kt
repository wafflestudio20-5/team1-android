package com.waffle22.wafflytime.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.ui.login.LoginViewModel
import com.waffle22.wafflytime.ui.login.SignUpEmailViewModel
import com.waffle22.wafflytime.ui.login.SignUpViewModel
import com.waffle22.wafflytime.ui.mainpage.MainHomeViewModel
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.TokenAuthenticator
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
                    .authenticator(TokenAuthenticator(get()))
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
            )
            .build()
    }

    single<WafflyApiService> {
        get<Retrofit>().create(WafflyApiService::class.java)
    }

    single { AuthStorage(get(), get()) }


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
    viewModel { SignUpEmailViewModel(get(), get()) }
    viewModel { MainHomeViewModel(get(), get(), get()) }
}

