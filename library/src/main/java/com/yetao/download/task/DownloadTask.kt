package com.yetao.download.task

import com.yetao.download.dispatcher.Dispatcher
import com.yetao.download.task.data.DownloadInfo
import com.yetao.download.task.inf.IDownloadTask
import com.yetao.download.task.inf.IRxTask
import com.yetao.download.task.rxjava.ParallelRxDownloadTask
import com.yetao.download.task.rxjava.RxDownloadTask
import com.yetao.download.task.rxjava.SerialRxDownloadTask
import java.util.HashMap

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
open class DownloadTask : IDownloadTask {
    private var urls: ArrayList<String> = arrayListOf()
    private var savePath: String? = null
    private var saveFileNames: HashMap<String, String?> = hashMapOf()
    private var intervalTime = 1000L
    private var priority = 10
    private val requestHeaders = hashMapOf<String, String>()
    private val responseHeaders = hashMapOf<String, String>()
    private var time = 0L//拦截时间

    override fun addRequestHeader(key: String, value: String): IDownloadTask {
        requestHeaders[key] = value
        return this
    }

    override fun removeRequestHeader(key: String) {
        requestHeaders.remove(key)
    }

    override fun getRequestHeader(key: String): String? = requestHeaders[key]

    override fun getAllRequestHeaders(): Map<String, String> = requestHeaders

    override fun addAllRequestHeaders(map: Map<String, String>) {
        requestHeaders.putAll(map)
    }

    override fun addResponseHeader(key: String, value: String): IDownloadTask {
        responseHeaders[key] = value
        return this
    }

    override fun getResponseHeader(key: String): String? = responseHeaders[key]

    override fun getAllResponseHeaders(): Map<String, String> = responseHeaders

    override fun addAllResponseHeaders(headers: Map<String, String>): IDownloadTask {
        responseHeaders.putAll(headers)
        return this
    }

    override fun getUrl(): String? = if (urls.isEmpty()) null else urls.last()

    override fun getUrls(): ArrayList<String> = urls

    override fun addUrl(url: String?, fileName: String?): IDownloadTask {
        url?.let {
            urls.add(it)
            saveFileNames.put(it, fileName)
        }
        return this
    }

    override fun getSavePath(): String? = savePath

    override fun setSavePath(path: String?): IDownloadTask {
        this.savePath = path
        return this
    }

    override fun getSaveFileName(): String? = saveFileNames[getUrl()]

    override fun getSaveFileNames(): HashMap<String, String?> = saveFileNames

    override fun setIntervalTime(intervalTime: Long): IDownloadTask {
        this.intervalTime = intervalTime
        return this
    }

    override fun getIntervalTime(): Long = intervalTime

    override fun setPriority(priority: Int): IDownloadTask {
        this.priority = priority
        return this
    }

    override fun getPriority(): Int = priority

    override fun single(): IRxTask<DownloadInfo> =
        single(null)


    override fun single(url: String?): IRxTask<DownloadInfo> = RxDownloadTask(this, url)

    override fun serial(): IRxTask<List<DownloadInfo>> =
        SerialRxDownloadTask(this)

    override fun parallel(): IRxTask<List<DownloadInfo>> =
        ParallelRxDownloadTask(this)

    override fun pause() {
        Dispatcher.with().cancel(getUrl()!!)
    }

    internal fun filterTime(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - time >= getIntervalTime()) {
            time = currentTime
            return true
        }
        return false
    }


}