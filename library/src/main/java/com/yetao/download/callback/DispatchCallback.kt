package com.yetao.download.callback

import com.yetao.download.dispatcher.Dispatcher
import com.yetao.download.task.data.DownloadInfo
import com.yetao.download.task.rxjava.RxDownloadTask
import io.reactivex.ObservableEmitter

/**
 *  Created by yetao on 2020/3/20
 *  description
 **/

class DispatchCallback(
    private val call: Callback,
    private val completedAction:()->Unit
) : Callback {
    override fun onStart() {
        call.onStart()
    }

    override fun onProgress(downloadInfo: DownloadInfo) {
        call.onProgress(downloadInfo)

    }

    override fun onCompleted() {
        completedAction.invoke()
        call.onCompleted()
    }

    override fun onError(e: Throwable) {
        call.onError(e)
    }


}