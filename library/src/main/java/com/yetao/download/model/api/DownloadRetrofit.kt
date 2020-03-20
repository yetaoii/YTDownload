package com.yetao.download.model.api

import com.yetao.download.manager.YTDownloadManager
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/

val defaultBaseUrl = "https://www.baidu.com"

val defaultOkHttpClient =
    OkHttpClient()
        .newBuilder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

val defaultCallAdapterFactory: CallAdapter.Factory? = RxJava2CallAdapterFactory.createWithScheduler(
    Schedulers.io()
)

var defaultConverterFactory: Converter.Factory? = GsonConverterFactory.create()


class DownloadRetrofit {
    companion object {
        val instance: DownloadRetrofit by lazy {
            DownloadRetrofit()
        }
    }

    inline fun <reified T> create(): T =
        Retrofit
            .Builder()
            .baseUrl(YTDownloadManager.instance.baseUrl ?: defaultBaseUrl)
            .client(YTDownloadManager.instance.okHttpClient ?: defaultOkHttpClient)
            .addConverterFactory(YTDownloadManager.instance.converterFactory?: defaultConverterFactory)
            .addCallAdapterFactory(
                YTDownloadManager.instance.callAdapterFactory ?: defaultCallAdapterFactory
            )
            .build()
            .create(T::class.java)
}