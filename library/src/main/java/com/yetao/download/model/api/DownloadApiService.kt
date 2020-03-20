package com.yetao.download.model.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
interface DownloadApiService{
    @GET
    @Streaming
    fun download(@Url url: String, @HeaderMap headers: Map<String, String>): Observable<Response<ResponseBody>>

    @GET
    @Streaming
    fun downloadProgress(@Url url: String, @HeaderMap headers: Map<String, String>): Observable<Response<ResponseBody>>
}