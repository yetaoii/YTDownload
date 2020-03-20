package com.yetao.download.callback

import com.yetao.download.task.data.DownloadInfo

/**
 *  Created by yetao on 2020/3/19
 *  description
 **/
interface Callback {
    fun onStart()
    fun onProgress(info: DownloadInfo)
    fun onCompleted()
    fun onError(e: Throwable)
}
