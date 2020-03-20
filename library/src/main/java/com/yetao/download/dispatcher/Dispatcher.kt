package com.yetao.download.dispatcher

import com.yetao.download.callback.DispatchCallback
import com.yetao.download.callback.DownloadCall
import com.yetao.download.exception.DownloadingErrorException
import com.yetao.download.exception.UrlErrorException
import com.yetao.download.executor.RetrofitExecutor
import com.yetao.download.model.store.SqlManager
import com.yetao.download.task.data.DownloadInfo
import com.yetao.download.util.log
import java.io.File
import java.util.concurrent.*

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
open class Dispatcher {
    companion object {
        //最大下载数
        var maxDownloadCount = 5
        //正在下载任务
        val downloadingTasks = ConcurrentHashMap<String, DownloadCall>()
        //等待下载队列 -数字越大，优先级越高
        val waitingTasks = PriorityBlockingQueue<DownloadCall>(11,
            Comparator<DownloadCall> { o1, o2 ->
                when {
                    o1.task.getPriority() < o2.task.getPriority() -> 1
                    o1.task.getPriority() > o2.task.getPriority() -> -1
                    else -> 0
                }
            })

        fun with() = Dispatcher()
    }

    @Synchronized
    open fun dispatch(call: DownloadCall) {
        if (!checkUrlEmpty(call)) {
            return
        }
        if (checkCompleteFromDb(call)) {
            return
        }
        if (checkDownloading(call)) {
            return
        }
        call.dispatcher = this
        if (downloadingTasks.size >= maxDownloadCount) {
            waitingTasks.offer(call)
            return
        }
        downloadingTasks[call.task.getUrl()!!] = call
        execute(call)
    }

    /**
     * 校验是否正在下载，正在下载直接抛异常
     */
    private fun checkDownloading(call: DownloadCall): Boolean {
        if (downloadingTasks.containsKey(call.task.getUrl())) {
            call.callback.onError(DownloadingErrorException())
            return true
        }
        return false
    }

    /**
     * call
     */
    private fun execute(call: DownloadCall) {
        "downloading task size: ${downloadingTasks.size}".log()
        "waiting task size: ${waitingTasks.size}".log()
        call.executor = RetrofitExecutor()
        call.callback = DispatchCallback(call.callback) {
            downloadingTasks.remove(call.task.getUrl())
            executeNext()
        }
        call.executor?.execute(call)
    }


    /**
     * 执行下一个任务
     */
    private fun executeNext() {
        if (waitingTasks.size > 0) {
            waitingTasks.poll()?.let {
                it.dispatcher?.dispatch(it)
            }
        }
    }

    /**
     * 校验是否已完成
     */
    private fun checkCompleteFromDb(call: DownloadCall): Boolean {
        val taskBody = SqlManager.instance.find(call.task.getUrl()!!)
        taskBody?.apply {
            val file = File(savePath)
            if (isFinish() && file.exists() && file.length() == taskBody.total) {
                call.callback.onProgress(
                    DownloadInfo(
                        call.task,
                        taskBody.progress,
                        taskBody.total
                    )
                )
                call.callback.onCompleted()
                return true
            }
        }
        return false
    }

    /**
     * 校验url空
     */
    private fun checkUrlEmpty(call: DownloadCall): Boolean {
        if (call.task.getUrl().isNullOrBlank()) {
            throw UrlErrorException("Url is null or blank.Please check url")
            return false
        }
        return true
    }


    fun cancel(url: String) {
        val downingCall = downloadingTasks[url]
        downingCall?.apply {
            executor?.cancel()
            downloadingTasks.remove(url)
        }
        waitingTasks.removeAll {
            it.task.getUrl() == url
        }
    }

    fun cancelAll() {
        for ((url, call) in downloadingTasks) {
            cancel(call.task.getUrl()!!)
        }
        for (call in waitingTasks) {
            cancel(call.task.getUrl()!!)
        }
    }
}
