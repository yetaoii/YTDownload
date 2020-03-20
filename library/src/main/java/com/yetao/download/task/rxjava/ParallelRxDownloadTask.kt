package com.yetao.download.task.rxjava

import com.yetao.download.dispatcher.Dispatcher
import com.yetao.download.task.DownloadTask
import com.yetao.download.task.IDownloadTask
import com.yetao.download.task.IRxTask
import com.yetao.download.task.data.DownloadInfo
import io.reactivex.Observable

/**
 *  Created by yetao on 2020/3/20
 *  description
 **/
class ParallelRxDownloadTask : RxDownloadTask, IRxTask {


    val list = arrayListOf<String?>()


    internal constructor()

    internal constructor(task: IDownloadTask) {
        for (url in task.getUrls()) {
            this.addUrl(url, task.getSaveFileNames()[url])
        }
        this.setSavePath(task.getSavePath())
        this.setIntervalTime(task.getIntervalTime())
        this.setPriority(task.getPriority())
        this.addAllRequestHeaders(task.getAllRequestHeaders())
    }

    override fun rxjava(): Observable<DownloadInfo> {
        var observables = arrayListOf<Observable<DownloadInfo>>()
        for (url in getUrls()) {
            observables.add(single(url).rxjava())
        }
        return Observable.merge<DownloadInfo>(observables)
    }

    override fun pause() {
        for (url in getUrls())
            Dispatcher.with().cancel(url)
    }
}