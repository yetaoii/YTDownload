package com.yetao.ytdownload

import android.app.Application
import com.yetao.download.manager.YTDownloadManager

/**
 *  Created by yetao on 2020/3/20
 *  description
 **/
class DemoApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        initYTDownload()
    }

    private fun initYTDownload() {
        YTDownloadManager.instance.init(this)//初始化
        YTDownloadManager.instance.debug = true//调试模式日志开关
        YTDownloadManager.instance.baseUrl//配置retrofit的baseUrl
        YTDownloadManager.instance.defaultSavePath = cacheDir.absolutePath
//        YTDownloadManager.instance.okHttpClient = //retrofit的okHttpClient
//        YTDownloadManager.instance.callAdapterFactory = //retrofit的callAdapterFactory
//        YTDownloadManager.instance.converterFactory = //retrofit的converterFactory
//        YTDownloadManager.instance.maxDownloadCount = 5//最大同时下载任务数，默认为5
    }
}