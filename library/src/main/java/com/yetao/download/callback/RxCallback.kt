package com.yetao.download.callback

import com.yetao.download.dispatcher.Dispatcher
import com.yetao.download.task.DownloadTask
import com.yetao.download.task.data.DownloadInfo
import com.yetao.download.task.rxjava.RxDownloadTask
import io.reactivex.ObservableEmitter

/**
 *  Created by yetao on 2020/3/20
 *  description
 **/

class RxCallback(
    private val emitter: ObservableEmitter<DownloadInfo>,
    private val task: DownloadTask
) : Callback {
    override fun onStart() {
        if (emitter.isDisposed) {
            Dispatcher.with().cancel(task.getUrl()!!)
            return
        }
    }

    override fun onProgress(downloadInfo: DownloadInfo) {
        if (emitter.isDisposed) {
            Dispatcher.with().cancel(task.getUrl()!!)
            return
        }
        emitter.onNext(downloadInfo)

    }

    override fun onCompleted() {
        if (emitter.isDisposed) {
            Dispatcher.with().cancel(task.getUrl()!!)
            return
        }
        emitter.onComplete()

    }

    override fun onError(e: Throwable) {
        if (emitter.isDisposed) {
            Dispatcher.with().cancel(task.getUrl()!!)
            return
        }
        emitter.onError(e)
    }
}
