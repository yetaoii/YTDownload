package com.yetao.download.task.rxjava

import com.yetao.download.dispatcher.Dispatcher
import com.yetao.download.task.DownloadTask
import com.yetao.download.task.inf.IDownloadTask
import com.yetao.download.task.inf.IRxTask
import com.yetao.download.task.data.DownloadInfo
import io.reactivex.Observable
import java.util.concurrent.ConcurrentHashMap

/**
 *  Created by yetao on 2020/3/20
 *  description
 **/
open class SerialRxDownloadTask : DownloadTask,
    IRxTask<List<DownloadInfo>> {

    private val infoMap = ConcurrentHashMap<String, DownloadInfo>()

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

    override fun rxjava(): Observable<List<DownloadInfo>> {
        val observables = ArrayList<Observable<DownloadInfo>>()
        for (url in getUrls()) {
            val task = single(url)
            if (task is IDownloadTask)
                infoMap[url] = DownloadInfo(task.setIntervalTime(0))
            observables.add(task.rxjava())
        }
        return Observable.concat<DownloadInfo>(observables)
            .flatMap {
                infoMap[it.task.getUrl()]?.apply {
                    currentBytes = it.currentBytes
                    totalBytes = it.totalBytes
                }
                Observable.just(it)
            }
            .filter {
                filterTime() ||(it.currentBytes == it.totalBytes && it.currentBytes >= 0 && it.totalBytes > 0)
            }.flatMap {
                Observable.just(infoMap.values.toList())
            }
    }


    override fun pause() {
        for (url in getUrls())
            Dispatcher.with().cancel(url)
    }
}