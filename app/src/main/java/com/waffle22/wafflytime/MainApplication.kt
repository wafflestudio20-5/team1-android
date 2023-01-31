package com.waffle22.wafflytime

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.waffle22.wafflytime.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }

        // Kakao SDK 초기화
        KakaoSdk.init(this, "cd623f2f02f10dfba7fc861bd8250859")
    }
}