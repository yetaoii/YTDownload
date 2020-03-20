package com.yetao.download.task.rxjava

import com.yetao.download.callback.DownloadCall
import com.yetao.download.callback.RxCallback
import com.yetao.download.dispatcher.RangeDispatcher
import com.yetao.download.task.DownloadTask
import com.yetao.download.task.IRxTask
import com.yetao.download.task.data.DownloadInfo
import io.reactivex.Observable

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
open class RxDownloadTask : DownloadTask, IRxTask {

    internal constructor()

    internal constructor(task: DownloadTask, url: String? = null) {
        this.addUrl(url ?: task.getUrl(), task.getSaveFileNames()[url ?: task.getUrl()])
        this.setSavePath(task.getSavePath())
        this.setIntervalTime(task.getIntervalTime())
        this.setPriority(task.getPriority())
        this.addAllRequestHeaders(task.getAllRequestHeaders())
    }

    override fun rxjava(): Observable<DownloadInfo> {
        return super.rxjava()
    }

}
