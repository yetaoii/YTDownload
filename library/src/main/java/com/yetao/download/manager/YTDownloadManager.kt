package com.yetao.download.manager

import android.app.Application
import android.content.Context
import android.os.Environment
import com.yetao.download.dispatcher.Dispatcher
import com.yetao.download.task.data.DownloadInfo
import com.yetao.download.task.IDownloadTask
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
class YTDownloadManager {
    var debug = false

    var context: Context? = null

    var defaultSavePath = Environment.getExternalStorageDirectory().absolutePath + "/Download"

    var baseUrl: String? = null

    var okHttpClient: OkHttpClient? = null

    var callAdapterFactory: CallAdapter.Factory? = null

    var converterFactory: Converter.Factory? = null

    companion object {

        val instance by lazy {
            YTDownloadManager()
        }
    }

    fun init(application: Application) {
        this.context = application
    }

    fun cancel(url:String?){
        url?.let {
            Dispatcher.with().cancel(it)
        }
    }

    fun cancelAll() {
        Dispatcher.with().cancelAll()
    }

    fun serial(vararg tasks: IDownloadTask): Observable<DownloadInfo> {
        return serial(tasks.toList())
    }

    fun serial(tasks: List<IDownloadTask>): Observable<DownloadInfo> {
        var observables = ArrayList<Observable<DownloadInfo>>()
        for (task in tasks) {
            observables.add(task.single(task.getUrl()).rxjava())
        }
        return Observable.concat<DownloadInfo>(observables)
    }

    fun parallel(vararg tasks: IDownloadTask): Observable<DownloadInfo> {
        return parallel(tasks.toList())
    }

    fun parallel(tasks: List<IDownloadTask>): Observable<DownloadInfo> {
        var observables = ArrayList<Observable<DownloadInfo>>()
        for (task in tasks) {
            observables.add(task.single(task.getUrl()).rxjava())
        }
        return Observable.merge<DownloadInfo>(observables)
    }
}