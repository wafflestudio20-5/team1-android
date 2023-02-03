package com.waffle22.wafflytime.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.ui.AuthCheckViewModel
import com.waffle22.wafflytime.ui.boards.boardlist.BoardListViewModel
import com.waffle22.wafflytime.ui.boards.boardscreen.BoardViewModel
import com.waffle22.wafflytime.ui.boards.boardscreen.SearchPostViewModel
import com.waffle22.wafflytime.ui.boards.newchat.NewChatViewModel
import com.waffle22.wafflytime.ui.boards.post.newpost.NewPostViewModel
import com.waffle22.wafflytime.ui.boards.post.PostViewModel
import com.waffle22.wafflytime.ui.login.LoginViewModel
import com.waffle22.wafflytime.ui.login.SignUpEmailViewModel
import com.waffle22.wafflytime.ui.login.SignUpViewModel
import com.waffle22.wafflytime.ui.mainpage.MainHomeViewModel
import com.waffle22.wafflytime.ui.notification.BaseNotificationViewModel
import com.waffle22.wafflytime.ui.notification.chat.list.ChatListViewModel
import com.waffle22.wafflytime.ui.notification.chat.room.ChatRoomViewModel
import com.waffle22.wafflytime.ui.preferences.LogoutViewModel
import com.waffle22.wafflytime.ui.preferences.SetNicknameViewModel
import com.waffle22.wafflytime.ui.preferences.SetProfilePicViewModel
import com.waffle22.wafflytime.ui.notification.notify.NotifyViewModel
import com.waffle22.wafflytime.ui.preferences.*
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.TokenInterceptor
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
            .addConverterFactory(MoshiConverterFactory.create(get()).asLenient())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor (
                        TokenInterceptor(sharedPreference, get(), get())
                    )
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
    // Auth
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { SignUpViewModel(get(), get(), get()) }
    viewModel { SignUpEmailViewModel(get(), get(), get()) }
    viewModel { AuthCheckViewModel(get(), get(), get()) }
    viewModel { SetNicknameViewModel(get(), get(), get()) }
    viewModel { SetProfilePicViewModel(get(), get(), get()) }
    viewModel { LogoutViewModel(get(), get(), get()) }
    viewModel { MypageEmailViewModel(get(), get(), get()) }

    // Main Home
    viewModel { MainHomeViewModel(get(), get(), get()) }

    // Boards
    viewModel { BoardListViewModel(get(), get()) }
    viewModel { BoardViewModel(get(), get()) }
    viewModel { PostViewModel(get()) }
    viewModel { NewPostViewModel(get(), get()) }
    viewModel { NewChatViewModel(get(), get()) }
    viewModel { SearchPostViewModel(get(), get()) }

    // Notification && Chat
    viewModel { BaseNotificationViewModel() }
    viewModel { NotifyViewModel(get(), get()) }
    viewModel { ChatListViewModel(get(), get()) }
    viewModel { (chatId: Long) -> ChatRoomViewModel(chatId, get(), get())}
}

