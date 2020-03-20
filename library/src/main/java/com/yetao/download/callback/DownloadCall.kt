package com.yetao.download.callback

import com.yetao.download.dispatcher.Dispatcher
import com.yetao.download.executor.Executor
import com.yetao.download.task.data.DownloadInfo
import com.yetao.download.task.IDownloadTask

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
class DownloadCall(
    var task: IDownloadTask,
    var callback: Callback,
    var dispatcher: Dispatcher?=null,
    var range: Boolean = false,
    var rangeStart: Long = 0L,
    var executor: Executor? = null,
    var downloadInfo: DownloadInfo
) {

    fun getFileSuffix(): String {
        val url = task.getUrl()
        url?.let {
            val mainUrl = url.split("?")[0]
            return mainUrl.substring(mainUrl.lastIndexOf("."))
        }
        return ""
    }
}

